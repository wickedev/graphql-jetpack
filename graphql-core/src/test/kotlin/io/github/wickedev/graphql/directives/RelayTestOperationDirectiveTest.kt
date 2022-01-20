package io.github.wickedev.graphql.directives

import com.expediagroup.graphql.generator.extensions.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class RelayTestOperationDirectiveTest : FunSpec({
    test("RELAY_TEST_OPERATION_DIRECTIVE_TYPE generate directive @relay_test_operation") {
        val schema = generateSchemaWithDirective(RELAY_TEST_OPERATION_DIRECTIVE_TYPE)

        println(schema.print())
        schema.print() shouldContain ("""
            ""${'"'}
            (Relay Only)

            Will have additional metadata that will contain GraphQL type info for fields in the operation's selection
            
            [Read More](https://relay.dev/docs/guides/testing-relay-components/#relay_test_operation)
            ""${'"'}
            directive @relay_test_operation on QUERY | MUTATION | SUBSCRIPTION
        """.trimIndent())
    }
})