package io.github.wickedev.graphql.directives

import com.expediagroup.graphql.generator.extensions.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class AppendNodeDirectiveTest : FunSpec({
    test("APPEND_NODE_DIRECTIVE_TYPE generate directive @appendNode") {
        val schema = generateSchemaWithDirective(APPEND_NODE_DIRECTIVE_TYPE)

        println(schema.print())
        schema.print() shouldContain ("""
            ""${'"'}
            (Relay Only)
            
            For use within mutations. After the mutation request is complete, this node
            will be appended to its parent connection.
            
            [Read More](https://relay.dev/docs/guided-tour/updating-data/graphql-mutations/#updating-data-once-a-request-is-complete)
            ""${'"'}
            directive @appendNode(connections: [ID!]!, edgeTypeName: String!) on FIELD
        """.trimIndent())
    }
})