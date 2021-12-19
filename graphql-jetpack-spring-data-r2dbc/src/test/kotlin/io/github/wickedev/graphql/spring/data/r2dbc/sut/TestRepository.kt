package io.github.wickedev.graphql.spring.data.r2dbc

import io.github.wickedev.graphql.interfaces.Node
import io.github.wickedev.graphql.scalars.ID
import io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces.GraphQLRepository
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.stereotype.Repository


@Table("users")
data class User(
    @Id override val id: ID,
    val name: String
): Node

@Repository
interface UserRepository : GraphQLRepository<User>
