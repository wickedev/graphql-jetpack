package io.github.wickedev.graphql.directives

import com.expediagroup.graphql.generator.extensions.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class DeleteRecordDirectiveTest : FunSpec({
    test("DELETE_RECORD_DIRECTIVE_TYPE generate directive @deleteRecord") {
        val schema = generateSchemaWithDirective(DELETE_RECORD_DIRECTIVE_TYPE)

        println(schema.print())
        schema.print() shouldContain ("""
            ""${'"'}
            (Relay Only)

            For use within mutations. After the mutation request is complete, this field
            will be removed from the store.

            [Read More](https://relay.dev/docs/guided-tour/updating-data/graphql-mutations/#updating-data-once-a-request-is-complete)
            ""${'"'}
            directive @deleteRecord on FIELD
        """.trimIndent())
    }
})