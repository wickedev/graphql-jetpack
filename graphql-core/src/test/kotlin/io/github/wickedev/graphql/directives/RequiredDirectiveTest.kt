package io.github.wickedev.graphql.directives

import com.expediagroup.graphql.generator.extensions.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class RequiredDirectiveTest : FunSpec({
    test("REQUIRED_DIRECTIVE_TYPE generate directive @required") {
        val schema = generateSchemaWithDirective(REQUIRED_DIRECTIVE_TYPE)

        println(schema.print())
        schema.print() shouldContain ("""
            ""${'"'}
            (Relay Only)
            
            `@required` is a directive you can add to fields in your Relay queries to
            declare how null values should be handled at runtime. You can think of it as
            saying "if this field is ever null, its parent field is invalid and should be
            null".
            
            [Read More](https://relay.dev/docs/guides/required-directive/)
            ""${'"'}
            directive @required(action: RequiredFieldAction!) on FIELD
        """.trimIndent())
    }
})