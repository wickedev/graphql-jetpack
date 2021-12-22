package io.github.wickedev.graphql.security

import com.expediagroup.graphql.generator.directives.KotlinFieldDirectiveEnvironment

val KotlinFieldDirectiveEnvironment.hasAuthDirective: Boolean
    get() = directive.name == AUTH_DIRECTIVE_NAME