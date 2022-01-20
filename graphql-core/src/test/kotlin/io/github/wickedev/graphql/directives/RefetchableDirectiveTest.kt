package io.github.wickedev.graphql.directives

import com.expediagroup.graphql.generator.extensions.print
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class RefetchableDirectiveTest : FunSpec({
    test("REFETCHABLE_DIRECTIVE_TYPE generate directive @refetchable") {
        val schema = generateSchemaWithDirective(REFETCHABLE_DIRECTIVE_TYPE)

        println(schema.print())
        schema.print() shouldContain ("""
            ""${'"'}
            (Relay Only)

            For use with [`useRefetchableFragment`](https://relay.dev/docs/api-reference/use-refetchable-fragment/).

            The @refetchable directive can only be added to fragments that are
            "refetchable", that is, on fragments that are declared on Viewer or Query
            types, or on a type that implements `Node` (i.e. a type that has an id).

            [Read More](https://relay.dev/docs/api-reference/use-refetchable-fragment/#arguments)
            ""${'"'}
            directive @refetchable(directives: [String!], queryName: String!) on FRAGMENT_DEFINITION
        """.trimIndent())
    }
})