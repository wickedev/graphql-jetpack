package io.github.wickedev.graphql.directives

import com.expediagroup.graphql.generator.extensions.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class DeleteEdgeDirectiveTest : FunSpec({
    test("DELETE_EDGE_DIRECTIVE_TYPE generate directive @deleteEdge") {
        val schema = generateSchemaWithDirective(DELETE_EDGE_DIRECTIVE_TYPE)

        println(schema.print())
        schema.print() shouldContain ("""
            ""${'"'}
            (Relay Only)

            For use within mutations. After the mutation request is complete, this edge
            will be removed from its parent connection.

            [Read More](https://relay.dev/docs/guided-tour/updating-data/graphql-mutations/#updating-data-once-a-request-is-complete)
            ""${'"'}
            directive @deleteEdge(connections: [ID!]!) on FIELD
        """.trimIndent())
    }
})