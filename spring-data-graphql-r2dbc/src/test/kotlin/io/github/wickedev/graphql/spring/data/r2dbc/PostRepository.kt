package io.github.wickedev.graphql.spring.data.r2dbc

import io.github.wickedev.graphql.interfaces.Node
import io.github.wickedev.graphql.scalars.ID
import io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces.GraphQLR2dbcRepository
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.stereotype.Repository

@Table("post")
data class Post(
    @Id override val id: ID,
    val title: String,
    val content: String
) : Node

@Repository
interface PostRepository : GraphQLR2dbcRepository<Post>
