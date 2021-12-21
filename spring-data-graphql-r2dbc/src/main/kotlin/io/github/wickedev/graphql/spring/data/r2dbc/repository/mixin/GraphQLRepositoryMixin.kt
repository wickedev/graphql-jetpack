package io.github.wickedev.graphql.spring.data.r2dbc.repository.mixin

import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.types.ID
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface GraphQLRepositoryMixin<T: Node> :
    GraphQLDataLoaderByIdRepositoryMixin<T>,
    GraphQLDataLoaderConnectionsRepositoryMixin<T>,
    R2dbcRepositoryMixin<T, ID>