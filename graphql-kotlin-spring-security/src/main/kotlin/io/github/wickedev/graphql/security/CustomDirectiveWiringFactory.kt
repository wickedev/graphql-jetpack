package io.github.wickedev.graphql.security

import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory

class CustomDirectiveWiringFactory(
    authSchemaDirectiveWiring: AuthSchemaDirectiveWiring
) : KotlinDirectiveWiringFactory(
    manualWiring = mapOf(
        AUTH_DIRECTIVE_NAME to authSchemaDirectiveWiring,
    )
)