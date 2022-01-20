package io.github.wickedev.graphql.directives

import com.expediagroup.graphql.generator.extensions.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class RawResponseTypeDirectiveTest : FunSpec({
    test("RAW_RESPONSE_TYPE_DIRECTIVE_TYPE generate directive @raw_response_type") {
        val schema = generateSchemaWithDirective(RAW_RESPONSE_TYPE_DIRECTIVE_TYPE)

        println(schema.print())
        schema.print() shouldContain ("""
            ""${'"'}
            (Relay only)

            A directive added to queries which tells Relay to generate types that cover
            the `optimisticResponse` parameter to `commitMutation`.

            [Read More](https://relay.dev/docs/glossary/#raw_response_type)
            ""${'"'}
            directive @raw_response_type on QUERY | MUTATION | SUBSCRIPTION
        """.trimIndent())
    }
})