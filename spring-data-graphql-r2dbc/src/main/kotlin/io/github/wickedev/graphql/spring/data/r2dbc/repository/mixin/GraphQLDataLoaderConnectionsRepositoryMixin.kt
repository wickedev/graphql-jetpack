package io.github.wickedev.graphql.spring.data.r2dbc.repository.mixin

import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.repository.GraphQLDataLoaderConnectionsRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface GraphQLDataLoaderConnectionsRepositoryMixin<T : Node>
    : GraphQLDataLoaderConnectionsRepository<T>,
    GraphQLDataLoaderForwardConnectionsRepositoryMixin<T>,
    GraphQLDataLoaderBackwardConnectionsRepositoryMixin<T>
