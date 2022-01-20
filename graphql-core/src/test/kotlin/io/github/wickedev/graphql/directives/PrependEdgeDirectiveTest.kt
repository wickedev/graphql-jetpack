package io.github.wickedev.graphql.directives

import com.expediagroup.graphql.generator.extensions.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class PrependEdgeDirectiveTest : FunSpec({
    test("PREPEND_EDGE_DIRECTIVE_TYPE generate directive @prependEdge") {
        val schema = generateSchemaWithDirective(PREPEND_EDGE_DIRECTIVE_TYPE)

        println(schema.print())
        schema.print() shouldContain ("""
            ""${'"'}
            (Relay Only)

            For use within mutations. After the mutation request is complete, this edge
            will be prepended to its parent connection.

            [Read More](https://relay.dev/docs/guided-tour/updating-data/graphql-mutations/#updating-data-once-a-request-is-complete)
            ""${'"'}
            directive @prependEdge(connections: [ID!]!) on FIELD
        """.trimIndent())
    }
})