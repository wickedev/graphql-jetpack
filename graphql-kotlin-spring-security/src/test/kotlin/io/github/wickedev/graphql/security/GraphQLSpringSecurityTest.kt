package io.github.wickedev.graphql.security

import com.expediagroup.graphql.server.types.GraphQLRequest
import io.kotest.core.spec.style.DescribeSpec
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser
import org.springframework.test.web.reactive.server.WebTestClient


@SpringBootTest
@AutoConfigureWebTestClient
class GraphQLSpringSecurityTest(
    private val webTestClient: WebTestClient
) : DescribeSpec({

    describe("GraphQLKotlin") {
        it("should be login") {
            webTestClient.post()
                .uri("/graphql")
                .bodyValue(
                    GraphQLRequest(
                        query = """
                            query Login(${'$'}email: String!, ${'$'}password: String!) {
                                login(email: ${'$'}email, password: ${'$'}password)
                            }
                        """.trimIndent(),
                        variables = mapOf(
                            "email" to "email@email.com",
                            "password" to "password"
                        )
                    )
                )
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(System.out::println)
        }

        it("should be protected") {
            webTestClient.post()
                .uri("/graphql")
                .bodyValue(
                    GraphQLRequest(
                        query = """
                            query {
                                protected
                            }
                        """.trimIndent(),
                        variables = emptyMap()
                    )
                )
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.errors[0].message").isEqualTo("UNAUTHENTICATED")
                .consumeWith(System.out::println)
        }

        it("should be allow by user") {
            webTestClient.mutateWith(mockUser().roles("USER"))
                .post()
                .uri("/graphql")
                .bodyValue(
                    GraphQLRequest(
                        query = """
                            query {
                                protected
                            }
                        """.trimIndent(),
                        variables = emptyMap()
                    )
                )
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.data.protected").isEqualTo(1)
                .consumeWith(System.out::println)
        }
    }
})