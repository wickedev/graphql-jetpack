package io.github.wickedev.graphql.spring.data.r2dbc.repository.mixin

import io.github.wickedev.graphql.interfaces.Node
import io.github.wickedev.graphql.scalars.ID
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryMixin
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface GraphQLRepositoryMixin<T: Node> :
    GraphQLDataLoaderRepositoryMixin<T, ID>,
    GraphQLDataLoaderNodeRepositoryMixin<T>,
    GraphQLDataLoaderConnectionsRepositoryMixin<T>,
    R2dbcRepositoryMixin<T, ID>