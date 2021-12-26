@file:Suppress("unused")

package io.github.wickedev.graphql

import com.expediagroup.graphql.generator.directives.KotlinFieldDirectiveEnvironment
import graphql.GraphQLContext
import graphql.schema.GraphQLDirective
import org.springframework.security.core.Authentication
import org.springframework.web.reactive.function.server.ServerRequest

val KotlinFieldDirectiveEnvironment.hasAuthDirective: Boolean
    get() = directive.name == AUTH_DIRECTIVE_NAME

val GraphQLDirective.requires: Array<String>
    @Suppress("UNCHECKED_CAST")
    get() = getArgument("requires").argumentValue.value as? Array<String> ?: emptyArray()

val KotlinFieldDirectiveEnvironment.requires: Array<String>
    get() = if (hasAuthDirective) directive.requires else emptyArray()

val GraphQLContext.authentication: Authentication?
    get() = getOrDefault("authentication", null)

val GraphQLContext.request: ServerRequest?
    get() = getOrDefault("request", null)
