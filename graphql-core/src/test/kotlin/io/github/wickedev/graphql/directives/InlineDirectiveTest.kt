package io.github.wickedev.graphql.directives

import com.expediagroup.graphql.generator.extensions.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class InlineDirectiveTest : FunSpec({
    test("INLINE_DIRECTIVE_TYPE generate directive @inline") {
        val schema = generateSchemaWithDirective(INLINE_DIRECTIVE_TYPE)

        println(schema.print())
        schema.print() shouldContain ("""
            ""${'"'}
            (Relay only)

            The hooks APIs that Relay exposes allow you to read data from the store only
            during the render phase. In order to read data from outside of the render
            phase (or from outside of React), Relay exposes the `@inline` directive. The
            data from a fragment annotated with `@inline` can be read using `readInlineData`.
            
            [Read More](https://relay.dev/docs/api-reference/graphql-and-directives/#inline)
            ""${'"'}
            directive @inline on FRAGMENT_DEFINITION
        """.trimIndent())
    }
})