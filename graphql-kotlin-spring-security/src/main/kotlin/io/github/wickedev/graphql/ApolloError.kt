@file:Suppress("unused")

package io.github.wickedev.graphql

import graphql.ErrorClassification
import graphql.GraphQLException

open class ApolloError(message: String?) : GraphQLException(message), ErrorClassification {

    class SyntaxError : ApolloError("GRAPHQL_PARSE_FAILED")

    class ValidationError : ApolloError("GRAPHQL_VALIDATION_FAILED")

    class UserInputError : ApolloError("BAD_USER_INPUT")

    class AuthenticationError : ApolloError("UNAUTHENTICATED")

    class ForbiddenError : ApolloError("FORBIDDEN")

    class PersistedQueryNotFoundError : ApolloError("PERSISTED_QUERY_NOT_FOUND")

    class PersistedQueryNotSupportedError : ApolloError("PERSISTED_QUERY_NOT_SUPPORTED")

    class TokenNotExistError : ApolloError("AUTHORIZATION_TOKEN_NOT_EXIST")
}
