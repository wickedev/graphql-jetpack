package io.github.wickedev.graphql.scalars

import graphql.language.StringValue
import io.github.wickedev.graphql.extentions.encodeBase64
import io.github.wickedev.graphql.types.ID
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class GraphQLIDScalarTest : DescribeSpec({

    describe("GraphQLIDScalar") {

        it("should parseValue correctly") {
            val serialized = GraphQLIDScalar.coercing.serialize(ID("user", "521"))

            serialized shouldBe "user:521".encodeBase64()
        }

        it("should parseValue correctly") {
            val id = GraphQLIDScalar.coercing.parseValue("user:521".encodeBase64())

            id.shouldBeInstanceOf<ID>()

            id.type shouldBe "user"
            id.value shouldBe "521"
        }

        it("should parseLiteral correctly") {
            val id =
                GraphQLIDScalar.coercing.parseLiteral(StringValue.newStringValue("user:521".encodeBase64()).build())

            id.shouldBeInstanceOf<ID>()

            id.type shouldBe "user"
            id.value shouldBe "521"
        }
    }
})