package io.github.wickedev.graphql.parser

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ToJsonPathTest : DescribeSpec({

    describe("toJsonPath") {

        it("should variables.files.0 to ${'$'}.variables.files[0]") {
            val path = "variables.files.0".toJsonPath()
            path shouldBe "${'$'}.variables.files[0]"
        }

        it("should 1.variables.files.0 to ${'$'}[1].variables.files[0]") {
            val path = "1.variables.files.0".toJsonPath()
            path shouldBe "${'$'}[1].variables.files[0]"
        }
    }
})