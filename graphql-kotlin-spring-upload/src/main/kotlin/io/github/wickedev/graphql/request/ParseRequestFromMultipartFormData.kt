package io.github.wickedev.graphql.request

import com.expediagroup.graphql.server.types.GraphQLBatchRequest
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.expediagroup.graphql.server.types.GraphQLServerRequest
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath.parse
import io.github.wickedev.coroutine.reactive.extensions.mono.await
import io.github.wickedev.graphql.types.Upload
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.web.reactive.function.server.ServerRequest

suspend fun parseRequestFromMultipartFormData(serverRequest: ServerRequest): GraphQLServerRequest? {
    val multipartData = serverRequest.multipartData().await() ?: return null
    val mapJson = (multipartData.getFirst("map") as? FormFieldPart)?.value() ?: return null
    val operationsJson = (multipartData.getFirst("operations") as? FormFieldPart)?.value() ?: return null
    val isBatchRequest = operationsJson.startsWith("[")
    val operationContext: DocumentContext = parse(operationsJson)
    val map: Map<String, List<String>> = parse(mapJson).json()

    map.entries.forEach {
        val path = it.value.firstOrNull()?.toJsonPath() ?: return@forEach
        val upload = (multipartData.getFirst(it.key) as? FilePart)?.let { file -> Upload(file) }
        operationContext.set(path, upload)
    }

    if (isBatchRequest) {
        val query: List<String> = operationContext.read("$[*].query")
        val variables: List<Map<String, Any?>> = operationContext.read("$[*].variables")
        return GraphQLBatchRequest(query.zip(variables).map {
            GraphQLRequest(query = it.first, variables = it.second)
        })
    }

    val query: String = operationContext.read("$.query")
    val variables: Map<String, Any?> = operationContext.read("$.variables")

    return GraphQLRequest(query = query, variables = variables)
}

