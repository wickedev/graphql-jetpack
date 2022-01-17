package io.github.wickedev.graphql.spring.data.r2dbc

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.annotations.Relation
import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.repository.GraphQLDataLoaderByIdRepository
import io.github.wickedev.graphql.types.ID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.stereotype.Repository
import java.util.concurrent.CompletableFuture

@Table("post")
data class Post(
    @Id override val id: ID,
    val title: String,
    val content: String,
    @Relation(User::class) val userId: ID,
) : Node {
    fun author(
        @Autowired userRepository: UserRepository,
        env: DataFetchingEnvironment
    ): CompletableFuture<User?> {
        return userRepository.findById(userId, env)
    }
}

@Repository
interface PostRepository : GraphQLDataLoaderByIdRepository<Post>
