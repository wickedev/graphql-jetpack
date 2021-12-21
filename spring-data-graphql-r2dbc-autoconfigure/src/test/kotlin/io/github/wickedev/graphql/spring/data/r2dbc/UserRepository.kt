package io.github.wickedev.graphql.spring.data.r2dbc

import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces.GraphQLR2dbcRepository
import io.github.wickedev.graphql.types.ID
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.stereotype.Repository

@Table("users")
data class User(
    @Id override val id: ID,
    val name: String
) : Node


@Repository
interface UserRepository : GraphQLR2dbcRepository<User>
