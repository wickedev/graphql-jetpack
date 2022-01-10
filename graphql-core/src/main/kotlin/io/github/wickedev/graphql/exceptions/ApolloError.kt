@file:Suppress("unused")

package io.github.wickedev.graphql.exceptions

import graphql.execution.ResultPath
import graphql.language.SourceLocation

open class ApolloError(code: String, message: String, path: ResultPath, sourceLocation: SourceLocation) :
    ApolloException(code, message, path, sourceLocation) {

    class SyntaxError(message: String, path: ResultPath, sourceLocation: SourceLocation) :
        ApolloError("GRAPHQL_PARSE_FAILED", message, path, sourceLocation)

    class ValidationError(message: String, path: ResultPath, sourceLocation: SourceLocation) :
        ApolloError("GRAPHQL_VALIDATION_FAILED", message, path, sourceLocation)

    class UserInputError(message: String, path: ResultPath, sourceLocation: SourceLocation) :
        ApolloError("BAD_USER_INPUT", message, path, sourceLocation)

    class AuthenticationError(message: String, path: ResultPath, sourceLocation: SourceLocation) :
        ApolloError("UNAUTHENTICATED", message, path, sourceLocation)

    class ForbiddenError(message: String, path: ResultPath, sourceLocation: SourceLocation) :
        ApolloError("FORBIDDEN", message, path, sourceLocation)

    class PersistedQueryNotFoundError(message: String, path: ResultPath, sourceLocation: SourceLocation) :
        ApolloError("PERSISTED_QUERY_NOT_FOUND", message, path, sourceLocation)

    class PersistedQueryNotSupportedError(message: String, path: ResultPath, sourceLocation: SourceLocation) :
        ApolloError("PERSISTED_QUERY_NOT_SUPPORTED", message, path, sourceLocation)

    class TokenNotExistError(message: String, path: ResultPath, sourceLocation: SourceLocation) :
        ApolloError("AUTHORIZATION_TOKEN_NOT_EXIST", message, path, sourceLocation)
}
