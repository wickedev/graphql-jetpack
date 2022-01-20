package io.github.wickedev.graphql.directives

import com.expediagroup.graphql.generator.extensions.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class UpdatableDirectiveTest : FunSpec({
    test("UPDATABLE_DIRECTIVE_TYPE generate directive @updatable") {
        val schema = generateSchemaWithDirective(UPDATABLE_DIRECTIVE_TYPE)

        println(schema.print())
        schema.print() shouldContain ("""
            ""${'"'}
            (Relay Only)
            
            Marks a given query or fragment as updatable.
            
            [Read More](https://fb.quip.com/4FZaADvkQPPl)
            ""${'"'}
            directive @updatable on QUERY | FRAGMENT_DEFINITION
        """.trimIndent())
    }
})