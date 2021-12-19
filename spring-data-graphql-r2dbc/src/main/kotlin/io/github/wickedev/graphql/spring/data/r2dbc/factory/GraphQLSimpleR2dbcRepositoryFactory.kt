@file:Suppress("DEPRECATION")

package io.github.wickedev.graphql.spring.data.r2dbc.factory

import io.github.wickedev.extentions.isAssignableFrom
import io.github.wickedev.graphql.spring.data.r2dbc.repository.SimpleGraphQLR2dbcRepository
import io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader.GraphQLDataLoaderConnectionsRepository
import io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader.GraphQLDataLoaderRepository
import io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces.GraphQLR2dbcRepository
import io.github.wickedev.graphql.spring.data.r2dbc.strategy.AdditionalIsNewStrategy
import org.springframework.data.mapping.context.MappingContext
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy
import org.springframework.data.r2dbc.core.StatementMapper
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactory
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty
import org.springframework.data.relational.repository.query.RelationalEntityInformation
import org.springframework.data.repository.core.RepositoryInformation
import org.springframework.data.repository.core.RepositoryMetadata
import org.springframework.data.repository.query.QueryLookupStrategy
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider
import org.springframework.lang.Nullable
import org.springframework.r2dbc.core.DatabaseClient
import java.util.*

class GraphQLSimpleR2dbcRepositoryFactory : R2dbcRepositoryFactory {
    private val databaseClient: DatabaseClient
    private val dataAccessStrategy: ReactiveDataAccessStrategy
    private val mappingContext: MappingContext<out RelationalPersistentEntity<*>, out RelationalPersistentProperty>
    private val converter: R2dbcConverter
    private val operations: R2dbcEntityOperations
    private val statementMapper: StatementMapper
    private val additionalIsNewStrategy: AdditionalIsNewStrategy?

    constructor(
        databaseClient: DatabaseClient,
        dataAccessStrategy: ReactiveDataAccessStrategy,
        additionalIsNewStrategy: AdditionalIsNewStrategy?,
    ) : super(
        databaseClient,
        dataAccessStrategy
    ) {
        this.databaseClient = databaseClient
        this.dataAccessStrategy = dataAccessStrategy
        this.converter = dataAccessStrategy.converter
        this.mappingContext = converter.mappingContext
        this.operations = R2dbcEntityTemplate(databaseClient, dataAccessStrategy)
        this.statementMapper = dataAccessStrategy.statementMapper
        this.additionalIsNewStrategy = additionalIsNewStrategy
    }

    constructor(
        operations: R2dbcEntityOperations,
        additionalIsNewStrategy: AdditionalIsNewStrategy?
    ) : super(operations) {
        this.databaseClient = operations.databaseClient
        this.dataAccessStrategy = operations.dataAccessStrategy
        this.converter = dataAccessStrategy.converter
        this.mappingContext = converter.mappingContext
        this.operations = operations
        this.statementMapper = dataAccessStrategy.statementMapper
        this.additionalIsNewStrategy = additionalIsNewStrategy
    }

    override fun getTargetRepository(information: RepositoryInformation): Any? {
        val entityInformation: RelationalEntityInformation<*, Any> = getEntityInformation(
            information.domainType,
            information
        )

        if (GraphQLDataLoaderRepository::class.isAssignableFrom(information.repositoryInterface)) {
            return getTargetRepositoryViaReflection(
                information,
                databaseClient,
                statementMapper,
                information,
                entityInformation,
                operations, converter
            )
        }

        return getTargetRepositoryViaReflection(
            information, entityInformation,
            operations, converter
        )
    }

    override fun <T : Any?, ID : Any?> getEntityInformation(domainClass: Class<T>): RelationalEntityInformation<T, ID> {
        return getEntityInformation(domainClass, null)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun <T, ID> getEntityInformation(
        domainClass: Class<T>,
        information: RepositoryInformation?
    ): RelationalEntityInformation<T, ID> {
        val entity = mappingContext.getRequiredPersistentEntity(domainClass)
        @Suppress("UNCHECKED_CAST")
        return GraphQLMappingRelationalEntityInformation(
            entity as RelationalPersistentEntity<T>,
            additionalIsNewStrategy
        )
    }

    override fun getRepositoryBaseClass(metadata: RepositoryMetadata): Class<*> {
        val repositoryInterface = metadata.repositoryInterface

        if (GraphQLR2dbcRepository::class.isAssignableFrom(repositoryInterface)) {
            return SimpleGraphQLR2dbcRepository::class.java
        }

        return super.getRepositoryBaseClass(metadata)
    }

    override fun getQueryLookupStrategy(
        @Nullable key: QueryLookupStrategy.Key?,
        evaluationContextProvider: QueryMethodEvaluationContextProvider
    ): Optional<QueryLookupStrategy> {
        return super.getQueryLookupStrategy(key, evaluationContextProvider)
    }
}
