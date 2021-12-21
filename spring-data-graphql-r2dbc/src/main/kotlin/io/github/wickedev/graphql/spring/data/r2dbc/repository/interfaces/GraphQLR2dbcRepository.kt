package io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces

import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.repository.GraphQLRepository
import io.github.wickedev.graphql.types.ID
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface GraphQLR2dbcRepository<T : Node> :
    GraphQLRepository<T>,
    R2dbcRepository<T, ID>