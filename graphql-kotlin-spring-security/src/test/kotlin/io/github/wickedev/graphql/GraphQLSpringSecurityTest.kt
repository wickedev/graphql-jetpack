package io.github.wickedev.graphql

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
                .consumeWith(System.out::println)
                .jsonPath("$.errors[0].message").isEqualTo("UNAUTHENTICATED")
        }

        it("should be protect to non role user") {
            webTestClient.mutateWith(mockUser().roles())
                .post()
                .uri("/graphql")
                .bodyValue(
                    GraphQLRequest(
                        query = """
                            query {
                                protectedWithRole
                            }
                        """.trimIndent(),
                        variables = emptyMap()
                    )
                )
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.errors[0].message").isEqualTo("FORBIDDEN")
        }

        it("should be allow to user(ROLE_USER)") {
            webTestClient.mutateWith(mockUser().roles("USER"))
                .post()
                .uri("/graphql")
                .bodyValue(
                    GraphQLRequest(
                        query = """
                            query {
                                protectedWithRole
                            }
                        """.trimIndent(),
                        variables = emptyMap()
                    )
                )
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.data.protectedWithRole").isEqualTo(1)
        }

        it("should be allow to user(ROLE_ADMIN)") {
            webTestClient.mutateWith(mockUser().roles("ADMIN"))
                .post()
                .uri("/graphql")
                .bodyValue(
                    GraphQLRequest(
                        query = """
                            query {
                                protectedWithRole
                            }
                        """.trimIndent(),
                        variables = emptyMap()
                    )
                )
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.data.protectedWithRole").isEqualTo(1)
        }

        it("should be non protect") {
            webTestClient.post()
                .uri("/graphql")
                .bodyValue(
                    GraphQLRequest(
                        query = """
                            query {
                                nonProtected
                            }
                        """.trimIndent(),
                        variables = emptyMap()
                    )
                )
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.data.nonProtected").isEqualTo(1)
        }
    }
})