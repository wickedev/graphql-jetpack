package io.github.wickedev.graphql

import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory

class AuthDirectiveWiringFactory(
    authSchemaDirectiveWiring: AuthSchemaDirectiveWiring
) : KotlinDirectiveWiringFactory(
    manualWiring = mapOf(
        AUTH_DIRECTIVE_NAME to authSchemaDirectiveWiring,
    )
)