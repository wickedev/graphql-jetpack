package io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces

import io.github.wickedev.graphql.interfaces.Node
import io.github.wickedev.graphql.scalars.ID
import io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader.GraphQLDataLoaderConnectionsRepository
import io.github.wickedev.graphql.spring.data.r2dbc.repository.dataloader.GraphQLDataLoaderRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository

@NoRepositoryBean
interface GraphQLRepository<T : Node> :
    GraphQLDataLoaderConnectionsRepository<T>,
    GraphQLDataLoaderRepository<T>,
    Repository<T, ID>