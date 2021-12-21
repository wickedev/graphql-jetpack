package io.github.wickedev.graphql.spring.data.r2dbc

import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.repository.GraphQLDataLoaderByIdRepository
import io.github.wickedev.graphql.types.ID
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
interface PostRepository : GraphQLDataLoaderByIdRepository<Post>
