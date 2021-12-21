package io.github.wickedev.graphql.request

import com.expediagroup.graphql.server.types.GraphQLServerRequest
import io.github.wickedev.coroutine.reactive.extensions.mono.await
import io.github.wickedev.graphql.parser.parseUploadRequest
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.web.reactive.function.server.ServerRequest

suspend fun requestFromMultipartFormData(serverRequest: ServerRequest): GraphQLServerRequest? {
    val multipartData = serverRequest.multipartData().await() ?: return null
    val mapJson = (multipartData.getFirst("map") as? FormFieldPart)?.value() ?: return null
    val operationsJson = (multipartData.getFirst("operations") as? FormFieldPart)?.value() ?: return null

    return parseUploadRequest(multipartData, operationsJson, mapJson)
}

