package io.github.wickedev.graphql.spring.data.r2dbc.repository.base


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
import reactor.core.publisher.Flux

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

    override fun getIdQuery(id: ID): Query {
        return Query.query(whereId().`is`(id as Any))
    }

    override fun getIdsInQuery(ids: Collection<ID>): Query {
        return Query.query(whereId().`in`(ids))
    }
}
