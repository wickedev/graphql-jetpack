package io.github.wickedev.graphql.request

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ParseTest : DescribeSpec({
    describe("Upload") {
        it("parse") {
            val path = "variables.files.0".toJsonPath()
            path shouldBe "files[0]"
        }
    }
})