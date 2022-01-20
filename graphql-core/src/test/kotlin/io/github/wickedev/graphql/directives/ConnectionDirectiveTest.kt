package io.github.wickedev.graphql.directives

import com.expediagroup.graphql.generator.extensions.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class ConnectionDirectiveTest : FunSpec({
    test("CONNECTION_DIRECTIVE_TYPE generate directive @connection") {
        val schema = generateSchemaWithDirective(CONNECTION_DIRECTIVE_TYPE)

        println(schema.print())
        schema.print() shouldContain ("""
            ""${'"'}
            (Relay Only)

            A directive which declares that a field implements the connection spec.

            [Read More](https://relay.dev/docs/guided-tour/list-data/pagination/)
            ""${'"'}
            directive @connection(dynamicKey_UNSTABLE: String, filters: [String], handler: String, key: String!) on FIELD
        """.trimIndent())
    }
})