package io.github.wickedev.graphql.request

import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.types.GraphQLBatchRequest
import com.expediagroup.graphql.server.types.GraphQLRequest
import io.github.wickedev.graphql.types.Upload
import kotlinx.coroutines.reactor.mono
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.RequestPredicates.POST
import org.springframework.web.reactive.function.server.ServerResponse.badRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

@Component
class UploadQuery : Query {
    fun uploads(files: List<Upload>): String = "${files.map { it.filename() }} Upload Successfully"

    fun upload(file: Upload): String = "${file.filename()} Upload Successfully"
}

@Component
class GraphQLUploadHandler {
    fun upload(serverRequest: ServerRequest): Mono<ServerResponse> = mono {
        when (val graphQLRequest = requestFromMultipartFormData(serverRequest)) {
            is GraphQLRequest -> ok().json().bodyValueAndAwait(process(graphQLRequest))
            is GraphQLBatchRequest -> ok().json().bodyValueAndAwait(process(graphQLRequest))
            else -> badRequest().buildAndAwait()
        }
    }

    private fun process(graphQLRequest: GraphQLBatchRequest): List<Map<String, Any?>> {
        return graphQLRequest.requests.map { process(it) }
    }

    private fun process(graphQLRequest: GraphQLRequest): Map<String, Any?> {
        val variables = graphQLRequest.variables ?: return emptyMap()
        return variables.entries.associate {
            return@associate when (val value = it.value) {
                is FilePart -> it.key to value.filename()
                is List<*> -> it.key to value.map { f -> print(f) }
                else -> it.key to null
            }
        }
    }

    private fun print(file: Any?): String? {
        return (file as? FilePart)?.filename()
    }
}

@SpringBootApplication
@EnableAutoConfiguration
class TestApplication {
    @Bean
    fun route(handler: GraphQLUploadHandler): RouterFunction<ServerResponse> {
        return RouterFunctions.route(POST("/graphql-upload"), handler::upload)
    }
}
