package io.github.wickedev.graphql.request

import io.kotest.core.spec.style.DescribeSpec
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters

@SpringBootTest
@AutoConfigureWebTestClient
class UploadWebRequestTest(
    private val webTestClient: WebTestClient
) : DescribeSpec({
    describe("Upload") {
        it("single upload request") {
            val bodyBuilder = MultipartBodyBuilder()

            bodyBuilder.part(
                "operations", """
                    { "query": "mutation (${'$'}file: Upload!) { singleUpload(file: ${'$'}file) { id } }", "variables": { "file": null } }
                """.trimIndent()
            )

            bodyBuilder.part(
                "map", """
                    { "0": ["variables.file"] }
                """.trimIndent()
            )

            bodyBuilder.part("0", ClassPathResource("a.txt"), MediaType.TEXT_PLAIN)

            webTestClient.post()
                .uri("/graphql")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.file").isEqualTo("a.txt")
                .consumeWith(System.out::println)
        }

        it("multiple upload request") {
            val bodyBuilder = MultipartBodyBuilder()

            bodyBuilder.part(
                "operations", """
                    { "query": "mutation(${'$'}files: [Upload!]!) { multipleUpload(files: ${'$'}files) { id } }", "variables": { "files": [null, null] } }
                """.trimIndent()
            )

            bodyBuilder.part(
                "map", """
                    { "0": ["variables.files.0"], "1": ["variables.files.1"] }
                """.trimIndent()
            )

            bodyBuilder.part("0", ClassPathResource("b.txt"), MediaType.TEXT_PLAIN)
            bodyBuilder.part("1", ClassPathResource("c.txt"), MediaType.TEXT_PLAIN)

            webTestClient.post()
                .uri("/graphql")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.files[0]").isEqualTo("b.txt")
                .jsonPath("$.files[1]").isEqualTo("c.txt")
                .consumeWith(System.out::println)
        }

        it("batching upload request") {
            val bodyBuilder = MultipartBodyBuilder()

            bodyBuilder.part(
                "operations", """
                    [{ "query": "mutation (${'$'}file: Upload!) { singleUpload(file: ${'$'}file) { id } }", "variables": { "file": null } }, { "query": "mutation(${'$'}files: [Upload!]!) { multipleUpload(files: ${'$'}files) { id } }", "variables": { "files": [null, null] } }]
                """.trimIndent()
            )

            bodyBuilder.part(
                "map", """
                    { "0": ["0.variables.file"], "1": ["1.variables.files.0"], "2": ["1.variables.files.1"] }
                """.trimIndent()
            )
            bodyBuilder.part("0", ClassPathResource("a.txt"), MediaType.TEXT_PLAIN)
            bodyBuilder.part("1", ClassPathResource("b.txt"), MediaType.TEXT_PLAIN)
            bodyBuilder.part("2", ClassPathResource("c.txt"), MediaType.TEXT_PLAIN)

            webTestClient.post()
                .uri("/graphql")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$[0].file").isEqualTo("a.txt")
                .jsonPath("$[1].files[0]").isEqualTo("b.txt")
                .jsonPath("$[1].files[1]").isEqualTo("c.txt")
                .consumeWith(System.out::println)
        }
    }
})