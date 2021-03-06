@file:Suppress("unused")

package io.github.wickedev.graphql

import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import graphql.introspection.Introspection
import org.springframework.security.access.prepost.PreAuthorize

const val AUTH_DIRECTIVE_NAME = "auth"

@GraphQLDirective(
    name = AUTH_DIRECTIVE_NAME, locations = [
        Introspection.DirectiveLocation.FIELD,
        Introspection.DirectiveLocation.FIELD_DEFINITION
    ]
)
annotation class Auth(
    val require: String = "isAuthenticated"
)