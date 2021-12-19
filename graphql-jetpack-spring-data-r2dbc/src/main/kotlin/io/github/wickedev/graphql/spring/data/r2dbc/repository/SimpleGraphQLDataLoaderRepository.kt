package io.github.wickedev.graphql.spring.data.r2dbc.repository

import io.github.wickedev.graphql.scalars.ID
import io.github.wickedev.graphql.spring.data.r2dbc.repository.base.PropertyBaseRepository
import io.github.wickedev.graphql.spring.data.r2dbc.repository.mixin.GraphQLDataLoaderRepositoryMixin
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryMixin
import org.springframework.data.relational.repository.query.RelationalEntityInformation
import org.springframework.data.repository.core.RepositoryInformation
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
class SimpleGraphQLDataLoaderRepository<T>(
    information: RepositoryInformation,
    entity: RelationalEntityInformation<T, ID>,
    entityOperations: R2dbcEntityOperations,
    converter: R2dbcConverter
) : PropertyBaseRepository<T, ID>(information, entity, entityOperations, converter),
    GraphQLDataLoaderRepositoryMixin<T, ID>,
    R2dbcRepositoryMixin<T, ID>