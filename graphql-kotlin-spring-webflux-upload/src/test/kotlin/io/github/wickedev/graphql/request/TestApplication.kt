package io.github.wickedev.graphql.request

import com.expediagroup.graphql.server.types.GraphQLBatchRequest
import com.expediagroup.graphql.server.types.GraphQLRequest
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.json

@SpringBootApplication
class TestApplication {
    @Bean
    fun graphQLRoutes() = coRouter {
        POST("graphql") { serverRequest ->
            when (val graphQLRequest = requestFromMultipartFormData(serverRequest)) {
                is GraphQLRequest -> ok().json().bodyValueAndAwait(process(graphQLRequest))
                is GraphQLBatchRequest -> ok().json().bodyValueAndAwait(process(graphQLRequest))
                else -> badRequest().buildAndAwait()
            }
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
}

private fun print(file: Any?): String? {
    return (file as? FilePart)?.filename()
}
