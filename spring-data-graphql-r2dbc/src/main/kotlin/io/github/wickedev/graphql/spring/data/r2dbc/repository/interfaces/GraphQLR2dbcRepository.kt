package io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces

import io.github.wickedev.graphql.interfaces.Node
import io.github.wickedev.graphql.scalars.ID
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface GraphQLR2dbcRepository<T : Node> :
    GraphQLRepository<T>,
    R2dbcRepository<T, ID>