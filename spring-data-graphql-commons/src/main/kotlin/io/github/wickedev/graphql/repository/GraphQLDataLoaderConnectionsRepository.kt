package io.github.wickedev.graphql.repository

import io.github.wickedev.graphql.interfases.Node
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface GraphQLDataLoaderConnectionsRepository<T : Node>
    : GraphQLDataLoaderForwardConnectionsRepository<T>,
    GraphQLDataLoaderBackwardConnectionsRepository<T>