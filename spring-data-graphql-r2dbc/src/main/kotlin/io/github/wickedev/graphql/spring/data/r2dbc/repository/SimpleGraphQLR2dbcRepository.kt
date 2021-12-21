package io.github.wickedev.graphql.spring.data.r2dbc.repository

import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.spring.data.r2dbc.repository.base.PropertyBaseRepository
import io.github.wickedev.graphql.spring.data.r2dbc.repository.mixin.GraphQLRepositoryMixin
import io.github.wickedev.graphql.types.ID
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.StatementMapper
import io.github.wickedev.graphql.spring.data.r2dbc.repository.mixin.R2dbcRepositoryMixin
import org.springframework.data.relational.repository.query.RelationalEntityInformation
import org.springframework.data.repository.core.RepositoryInformation
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
class SimpleGraphQLR2dbcRepository<T : Node>(
    databaseClient: DatabaseClient,
    statementMapper: StatementMapper,
    information: RepositoryInformation,
    entity: RelationalEntityInformation<T, ID>,
    entityOperations: R2dbcEntityOperations,
    converter: R2dbcConverter
) : PropertyBaseRepository<T, ID>(
    databaseClient,
    statementMapper,
    information,
    entity,
    entityOperations,
    converter
), GraphQLRepositoryMixin<T>,
    R2dbcRepositoryMixin<T, ID>