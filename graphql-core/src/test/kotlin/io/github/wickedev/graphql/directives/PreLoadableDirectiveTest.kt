package io.github.wickedev.graphql.directives

import com.expediagroup.graphql.generator.extensions.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class PreLoadableDirectiveTest : FunSpec({
    test("PRE_LOADABLE_DIRECTIVE_TYPE generate directive @preloadable") {
        val schema = generateSchemaWithDirective(PRE_LOADABLE_DIRECTIVE_TYPE)

        println(schema.print())
        schema.print() shouldContain ("""
            ""${'"'}
            (Relay Only)

            A directive that modifies queries and which causes Relay to generate
            `${'$'}Parameters.js` files and preloadable concrete requests. Required if the
            query is going to be used as part of an entry point.

            The `hackPreloader` argument is FB only and generates a Hack preloader file.

            [Read More](https://relay.dev/docs/glossary/#preloadable)
            ""${'"'}
            directive @preloadable(hackPreloader: Boolean = false) on QUERY
        """.trimIndent())
    }
})