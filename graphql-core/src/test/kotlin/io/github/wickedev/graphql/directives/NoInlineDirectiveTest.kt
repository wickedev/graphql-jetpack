package io.github.wickedev.graphql.directives

import com.expediagroup.graphql.generator.extensions.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class NoInlineDirectiveTest : FunSpec({
    test("NO_INLINE_DIRECTIVE_TYPE generate directive @no_inline") {
        val schema = generateSchemaWithDirective(NO_INLINE_DIRECTIVE_TYPE)

        println(schema.print())
        schema.print() shouldContain ("""
            ""${'"'}
            (Relay only)

            which can be used to prevent common fragments from being inlined, resulting in smaller generated files.
            ""${'"'}
            directive @no_inline(raw_response_type: Boolean) on FRAGMENT_DEFINITION
        """.trimIndent())
    }
})