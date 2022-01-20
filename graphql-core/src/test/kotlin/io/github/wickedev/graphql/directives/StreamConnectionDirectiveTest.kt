package io.github.wickedev.graphql.directives

import com.expediagroup.graphql.generator.extensions.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class StreamConnectionDirectiveTest : FunSpec({
    test("STREAM_CONNECTION_DIRECTIVE_TYPE generate directive @stream_connection") {
        val schema = generateSchemaWithDirective(STREAM_CONNECTION_DIRECTIVE_TYPE)

        println(schema.print())
        schema.print() shouldContain ("""
            ""${'"'}
            (Relay Only)

            A directive which declares that a field implements the connection spec.

            [Read More](https://relay.dev/docs/guided-tour/list-data/pagination/)
            ""${'"'}
            directive @stream_connection(dynamicKey_UNSTABLE: String, filters: [String], handler: String, if: Boolean = true, initial_count: Int!, key: String!, use_customized_batch: Boolean = false) on FIELD
        """.trimIndent())
    }
})