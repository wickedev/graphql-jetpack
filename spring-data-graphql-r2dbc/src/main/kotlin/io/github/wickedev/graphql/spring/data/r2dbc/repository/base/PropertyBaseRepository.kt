package io.github.wickedev.graphql.spring.data.r2dbc.repository.base


import io.github.wickedev.graphql.types.ID
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.StatementMapper
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.repository.query.RelationalEntityInformation
import org.springframework.data.relational.repository.query.RelationalExampleMapper
import org.springframework.data.repository.core.RepositoryInformation
import org.springframework.data.util.Lazy
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

abstract class PropertyBaseRepository<T, ID>(
    final override val databaseClient: DatabaseClient,
    final override val statementMapper: StatementMapper,
    final override val information: RepositoryInformation,
    final override val entity: RelationalEntityInformation<T, ID>,
    final override val entityOperations: R2dbcEntityOperations,
    final override val converter: R2dbcConverter
) : PropertyRepository<T, ID> {
    final override val idProperty: Lazy<RelationalPersistentProperty> = Lazy.of {
        converter
            .mappingContext
            .getRequiredPersistentEntity(this.entity.javaType)
            .requiredIdProperty
    }
    final override val exampleMapper: RelationalExampleMapper = RelationalExampleMapper(converter.mappingContext)

    override val tableName: String
        get() = entity.tableName.reference

    override fun getIdProperty(): RelationalPersistentProperty {
        return idProperty.get()
    }

    override fun whereId(): Criteria.CriteriaStep {
        return Criteria.where(getIdProperty().name)
    }

    override fun emptyQuery(): Query {
        return Query.empty()
    }

    override fun query(criteria: Criteria?): Query {
        return if (criteria == null) Query.empty() else Query.query(criteria)
    }

    override fun getIdQuery(id: ID): Query {
        return Query.query(whereId().`is`(id as Any))
    }

    override fun getIdsInQuery(ids: Collection<ID>): Query {
        return Query.query(whereId().`in`(ids))
    }

    private fun whereIdGreaterThanCriteria(after: ID?): Criteria? {
        return after?.let { whereId().greaterThan(it as Any) }
    }

    override fun whereIdGreaterThanQuery(after: ID?, criteria: Criteria?): Query {
        val combinedCriteria = Criteria.from(
            listOfNotNull(
                whereIdGreaterThanCriteria(after),
                criteria,
            )
        )

        return Query.query(combinedCriteria)
    }

    private fun whereIdLessThanCriteria(before: ID?): Criteria? {
        return before?.let { whereId().lessThan(it as Any)}
    }

    override fun whereIdLessThanQuery(before: ID?, criteria: Criteria?): Query {
        val combinedCriteria = Criteria.from(
            listOfNotNull(
                whereIdLessThanCriteria(before),
                criteria,
            )
        )

        return Query.query(combinedCriteria)
    }


    override fun findFirst(sort: Sort): Mono<T?> {
        return entityOperations.select(
            emptyQuery().sort(sort).limit(1), entity.javaType
        ).toMono()
    }
}
