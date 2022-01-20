package io.github.wickedev.graphql.directives

import com.expediagroup.graphql.generator.extensions.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class RelayDirectiveTest : FunSpec({
    test("RELAY_DIRECTIVE_TYPE generate directive @relay") {
        val schema = generateSchemaWithDirective(RELAY_DIRECTIVE_TYPE)

        println(schema.print())
        schema.print() shouldContain ("""
            ""${'"'}
            (Relay Only)

            A directive that allows you to turn off Relay's data masking.

            Read more
            [here](https://relay.dev/docs/api-reference/graphql-and-directives/#relayplural-boolean)
            and
            [here](https://relay.dev/docs/api-reference/graphql-and-directives/#relaymask-boolean).
            ""${'"'}
            directive @relay(
                "Marks a fragment spread which should be unmasked if provided false"
                mask: Boolean = true, 
                "Marks a fragment as being backed by a GraphQLList."
                plural: Boolean
              ) on FRAGMENT_DEFINITION | FRAGMENT_SPREAD
        """.trimIndent())
    }
})