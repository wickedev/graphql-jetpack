package io.github.wickedev.graphql.directives

import com.expediagroup.graphql.generator.extensions.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class AssignableDirectiveTest : FunSpec({
    test("ASSIGNABLE_DIRECTIVE_TYPE generate directive @assignable") {
        val schema = generateSchemaWithDirective(ASSIGNABLE_DIRECTIVE_TYPE)

        println(schema.print())
        schema.print() shouldContain ("""
            ""${'"'}
            (Relay Only)

            Marks a given fragment as assignable.

            [Read More](https://fb.quip.com/4FZaADvkQPPl)
            ""${'"'}
            directive @assignable on FRAGMENT_DEFINITION
        """.trimIndent())
    }
})