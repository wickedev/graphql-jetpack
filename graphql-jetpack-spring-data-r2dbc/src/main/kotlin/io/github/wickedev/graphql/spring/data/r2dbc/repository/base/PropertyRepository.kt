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

interface PropertyRepository<T, ID> {
    val databaseClient: DatabaseClient
    val statementMapper: StatementMapper
    val information: RepositoryInformation
    val entity: RelationalEntityInformation<T, ID>
    val entityOperations: R2dbcEntityOperations
    val converter: R2dbcConverter
    val idProperty: Lazy<RelationalPersistentProperty>
    val exampleMapper: RelationalExampleMapper
    val tableName: String

    fun getIdProperty(): RelationalPersistentProperty

    fun whereId(): Criteria.CriteriaStep

    fun emptyQuery(): Query

    fun getIdQuery(id: ID): Query

    fun getIdsInQuery(ids: Collection<ID>): Query
}
