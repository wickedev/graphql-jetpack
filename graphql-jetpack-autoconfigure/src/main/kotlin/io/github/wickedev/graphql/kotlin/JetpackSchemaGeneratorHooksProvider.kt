package io.github.wickedev.graphql.kotlin

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.plugin.schema.hooks.SchemaGeneratorHooksProvider

@Suppress("unused")
class JetpackSchemaGeneratorHooksProvider: SchemaGeneratorHooksProvider {
    override fun hooks(): SchemaGeneratorHooks {
        val configuration = JetpackGraphQLSchemaConfiguration()
        return JetpackSchemaGeneratorHooks(
            configuration.customScalars(),
            configuration.authSchemaDirectiveWiring()
        )
    }
}