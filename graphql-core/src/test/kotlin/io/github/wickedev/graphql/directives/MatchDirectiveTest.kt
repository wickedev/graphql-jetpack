package io.github.wickedev.graphql.directives

import com.expediagroup.graphql.generator.extensions.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class MatchDirectiveTest : FunSpec({
    test("MATCH_DIRECTIVE_TYPE generate directive @match") {
        val schema = generateSchemaWithDirective(MATCH_DIRECTIVE_TYPE)

        println(schema.print())
        schema.print() shouldContain ("""
            ""${'"'}
            (Relay Only)

            A directive that, when used in combination with `@module`, allows users to
            download specific JS components alongside the rest of the GraphQL payload if
            the field decorated with [`@match`](https://relay.dev/docs/glossary/#match)
            has a certain type. See [3D](https://relay.dev/docs/glossary/#3d).

            [Read More](https://relay.dev/docs/glossary/#match)
            ""${'"'}
            directive @match(key: String) on FIELD
        """.trimIndent())
    }
})