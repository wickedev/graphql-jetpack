package io.github.wickedev.graphql.spring.data.r2dbc.repository.base


import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.repository.query.RelationalEntityInformation
import org.springframework.data.relational.repository.query.RelationalExampleMapper
import org.springframework.data.repository.core.RepositoryInformation
import org.springframework.data.util.Lazy

abstract class PropertyBaseRepository<T, ID>(
    final override val information: RepositoryInformation,
    final override val entity: RelationalEntityInformation<T, ID>,
    final override val entityOperations: R2dbcEntityOperations,
    converter: R2dbcConverter
) : PropertyRepository<T, ID> {
    final override val idProperty: Lazy<RelationalPersistentProperty>
    final override val exampleMapper: RelationalExampleMapper

    init {
        idProperty = Lazy.of {
            converter
                .mappingContext
                .getRequiredPersistentEntity(this.entity.javaType)
                .requiredIdProperty
        }
        exampleMapper = RelationalExampleMapper(converter.mappingContext)
    }

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

    override fun getIdsQuery(ids: List<ID>): Query {
        return Query.query(whereId().`in`(ids))
    }
}
