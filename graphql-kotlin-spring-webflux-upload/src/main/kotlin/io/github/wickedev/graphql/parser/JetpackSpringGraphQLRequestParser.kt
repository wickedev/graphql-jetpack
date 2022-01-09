package io.github.wickedev.graphql.parser

import com.expediagroup.graphql.server.spring.execution.SpringGraphQLRequestParser
import com.expediagroup.graphql.server.types.GraphQLServerRequest
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.wickedev.coroutine.reactive.extensions.mono.await
import io.github.wickedev.graphql.extentions.unwrap
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.web.reactive.function.server.ServerRequest

class JetpackSpringGraphQLRequestParser(objectMapper: ObjectMapper): SpringGraphQLRequestParser(objectMapper) {

    override suspend fun parseRequest(request: ServerRequest): GraphQLServerRequest? {
        val mediaType = request.headers().contentType().unwrap() ?: return super.parseRequest(request)

        return when (MediaType.MULTIPART_FORM_DATA) {
            MediaType(mediaType.type, mediaType.subtype) -> getRequestFromMultipartFormData(request)
            else -> super.parseRequest(request)
        }
    }

    private suspend fun getRequestFromMultipartFormData(serverRequest: ServerRequest): GraphQLServerRequest? {
        val multipartData = serverRequest.multipartData().await() ?: return null
        val operationsJson = (multipartData.getFirst("operations") as FormFieldPart).value() ?: return null
        val mapJson = (multipartData.getFirst("map") as FormFieldPart).value() ?: return null
        return parseUploadRequest(multipartData, operationsJson, mapJson)
    }
}