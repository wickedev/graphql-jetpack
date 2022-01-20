package io.github.wickedev.graphql.directives

import com.expediagroup.graphql.generator.extensions.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class ModuleDirectiveTest : FunSpec({
    test("MODULE_DIRECTIVE_TYPE generate directive @module") {
        val schema = generateSchemaWithDirective(MODULE_DIRECTIVE_TYPE)

        println(schema.print())
        schema.print() shouldContain ("""
            ""${'"'}
            (Relay Only)

            A directive that, when used in combination with
            [`@match`](https://relay.dev/docs/glossary/#match), allows users to specify
            which JS components to download if the field decorated with @match has a
            certain type. See [3D](https://relay.dev/docs/glossary/#3d).

            [Read More](https://relay.dev/docs/glossary/#module)
            ""${'"'}
            directive @module(name: String!) on FRAGMENT_SPREAD
        """.trimIndent())
    }
})