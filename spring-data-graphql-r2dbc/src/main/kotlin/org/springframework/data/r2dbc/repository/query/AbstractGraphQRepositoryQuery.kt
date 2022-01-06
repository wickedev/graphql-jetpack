@file:Suppress("DEPRECATION", "MemberVisibilityCanBePrivate")

package org.springframework.data.r2dbc.repository.query

import org.reactivestreams.Publisher
import org.springframework.data.domain.Sort
import org.springframework.data.mapping.model.EntityInstantiators
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy
import org.springframework.data.relational.repository.query.RelationalParameterAccessor
import org.springframework.data.relational.repository.query.RelationalParameters
import org.springframework.data.repository.query.ResultProcessor
import org.springframework.data.repository.query.ReturnedType
import org.springframework.data.repository.query.parser.PartTree
import org.springframework.r2dbc.core.PreparedOperation
import org.springframework.r2dbc.core.RowsFetchSpec
import reactor.core.publisher.Mono

abstract class AbstractGraphQRepositoryQuery(
    protected val method: R2dbcQueryMethod,
    protected val entityOperations: R2dbcEntityOperations,
    protected val converter: R2dbcConverter,
    protected val dataAccessStrategy: ReactiveDataAccessStrategy
) : AbstractR2dbcQuery(method, entityOperations, converter) {

    protected val instantiators: EntityInstantiators = EntityInstantiators()
    protected val processor: ResultProcessor = method.resultProcessor
    protected val parameters: RelationalParameters = method.parameters

    protected val tree: PartTree by lazy {
        try {
            val tree = PartTree(method.name, method.entityInformation.javaType)
            R2dbcQueryCreator.validate(tree, this.parameters)
            return@lazy tree
        } catch (e: RuntimeException) {
            throw IllegalArgumentException(
                String.format("Failed to create query for method %s! %s", method, e.message), e
            )
        }
    }

    override fun isModifyingQuery(): Boolean = false

    override fun isCountQuery(): Boolean = false

    override fun isExistsQuery(): Boolean = false

    fun execute(fetchSpec: RowsFetchSpec<*>): Publisher<*> {
        val execution: R2dbcQueryExecution = R2dbcQueryExecution.ResultProcessingExecution(
            getExecutionToWrap(),
            R2dbcQueryExecution.ResultProcessingConverter(processor, converter.mappingContext, instantiators)
        )
        @Suppress("UNCHECKED_CAST")
        return execution.execute(RowsFetchSpec::class.java.cast(fetchSpec) as RowsFetchSpec<Any>)
    }

    override fun createQuery(accessor: RelationalParameterAccessor): Mono<PreparedOperation<*>> {
        return Mono.fromSupplier {
            val returnedType: ReturnedType = processor.withDynamicProjection(accessor).returnedType
            var projectedProperties: List<String?> = emptyList<String>()
            if (returnedType.needsCustomConstruction()) {
                projectedProperties = ArrayList(returnedType.inputProperties)
            }
            val entityMetadata = queryMethod.entityInformation
            val queryCreator = R2dbcQueryCreator(
                tree, dataAccessStrategy, entityMetadata, accessor,
                projectedProperties
            )
            queryCreator.createQuery(getDynamicSort(accessor))
        }
    }

    override fun resolveResultType(resultProcessor: ResultProcessor): Class<*> {
        val returnedType = resultProcessor.returnedType
        if (returnedType.returnedType.isAssignableFrom(returnedType.domainType)) {
            return returnedType.domainType
        }
        return if (returnedType.isProjecting) returnedType.domainType else returnedType.returnedType
    }

    private fun getDynamicSort(accessor: RelationalParameterAccessor): Sort {
        return if (parameters.potentiallySortsDynamically()) accessor.sort else Sort.unsorted()
    }

    private fun getExecutionToWrap(): R2dbcQueryExecution {
        return if (isCountQuery) {
            return R2dbcQueryExecution { q -> q.first().defaultIfEmpty(0L) }
        } else if (isExistsQuery) {
            return R2dbcQueryExecution { q -> q.first().defaultIfEmpty(false) }
        } else if (method.isCollectionQuery) {
            R2dbcQueryExecution { q -> q.all() }
        } else {
            R2dbcQueryExecution { q -> q.one() }
        }
    }
}