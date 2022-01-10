@file:Suppress("unused")

package io.github.wickedev.graphql.exceptions

import graphql.execution.ResultPath
import graphql.language.SourceLocation

open class ApolloError(message: String, path: ResultPath, sourceLocation: SourceLocation) :
    ApolloException(message, path, sourceLocation) {

    class SyntaxError(path: ResultPath, sourceLocation: SourceLocation) :
        ApolloError("GRAPHQL_PARSE_FAILED", path, sourceLocation)

    class ValidationError(path: ResultPath, sourceLocation: SourceLocation) :
        ApolloError("GRAPHQL_VALIDATION_FAILED", path, sourceLocation)

    class UserInputError(path: ResultPath, sourceLocation: SourceLocation) :
        ApolloError("BAD_USER_INPUT", path, sourceLocation)

    class AuthenticationError(path: ResultPath, sourceLocation: SourceLocation) :
        ApolloError("UNAUTHENTICATED", path, sourceLocation)

    class ForbiddenError(path: ResultPath, sourceLocation: SourceLocation) : ApolloError("FORBIDDEN", path, sourceLocation)

    class PersistedQueryNotFoundError(path: ResultPath, sourceLocation: SourceLocation) :
        ApolloError("PERSISTED_QUERY_NOT_FOUND", path, sourceLocation)

    class PersistedQueryNotSupportedError(path: ResultPath, sourceLocation: SourceLocation) :
        ApolloError("PERSISTED_QUERY_NOT_SUPPORTED", path, sourceLocation)

    class TokenNotExistError(path: ResultPath, sourceLocation: SourceLocation) :
        ApolloError("AUTHORIZATION_TOKEN_NOT_EXIST", path, sourceLocation)
}
