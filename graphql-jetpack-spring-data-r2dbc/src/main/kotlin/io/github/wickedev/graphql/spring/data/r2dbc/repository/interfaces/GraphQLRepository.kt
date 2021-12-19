package io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces

import io.github.wickedev.graphql.interfaces.Node
import io.github.wickedev.graphql.scalars.ID
import io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader.GraphQLDataLoaderConnectionsRepository
import io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader.GraphQLDataLoaderNodeRepository
import io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader.GraphQLDataLoaderRepository
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface GraphQLRepository<T : Node> :
    GraphQLDataLoaderNodeRepository<T>,
    GraphQLDataLoaderConnectionsRepository<T>,
    GraphQLDataLoaderRepository<T, ID>,
    R2dbcRepository<T, ID>