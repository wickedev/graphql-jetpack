package io.github.wickedev.graphql.parser

import com.expediagroup.graphql.server.types.GraphQLBatchRequest
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.expediagroup.graphql.server.types.GraphQLServerRequest
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath.parse
import io.github.wickedev.graphql.types.Upload
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.Part
import org.springframework.util.MultiValueMap

fun parseUploadRequest(
    multipartData: MultiValueMap<String, Part>,
    operationsJson: String,
    mapJson: String,
): GraphQLServerRequest {
    val isBatchRequest = operationsJson.startsWith("[")

    val operationContext: DocumentContext = parse(operationsJson)
    val map: Map<String, List<String>> = parse(mapJson).json()

    map.entries.forEach {
        val path = it.value.firstOrNull()?.toJsonPath() ?: return@forEach
        val upload = (multipartData.getFirst(it.key) as? FilePart)?.let { file -> Upload(file) }
        operationContext.set(path, upload)
    }

    if (isBatchRequest) {
        val queries: List<String> = operationContext.read("$[*].query")
        val variables: List<Map<String, Any?>> = operationContext.read("$[*].variables")
        return GraphQLBatchRequest(queries.zip(variables).map {
            GraphQLRequest(query = it.first, variables = it.second)
        })
    }

    val query: String = operationContext.read("$.query")
    val variables: Map<String, Any?> = operationContext.read("$.variables")

    return GraphQLRequest(query = query, variables = variables)
}
