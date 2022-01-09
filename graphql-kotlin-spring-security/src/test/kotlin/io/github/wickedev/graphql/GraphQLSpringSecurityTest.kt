package io.github.wickedev.graphql

import com.expediagroup.graphql.server.types.GraphQLRequest
import io.kotest.core.spec.style.DescribeSpec
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration


@SpringBootTest
@AutoConfigureWebTestClient
class GraphQLSpringSecurityTest(
    private val webTestClient: WebTestClient
) : DescribeSpec({

    describe("GraphQL Kotlin Spring Security") {

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
            webTestClient
                .mutateWith(mockUser().roles())
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
            webTestClient
                .mutate()
                .responseTimeout(Duration.ofDays(1))
                .build()
                .mutateWith(mockUser().roles("ADMIN"))
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

        it("public query should be non protect") {
            webTestClient.post()
                .uri("/graphql")
                .bodyValue(
                    GraphQLRequest(
                        query = """
                            query {
                                public
                            }
                        """.trimIndent(),
                        variables = emptyMap()
                    )
                )
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.data.public").isEqualTo(1)
        }

        it("should allow protectedWithParam 1") {
            webTestClient.mutateWith(mockUser())
                .post()
                .uri("/graphql")
                .bodyValue(
                    GraphQLRequest(
                        query = """
                            query {
                                protectedWithParam(param: 1)
                            }
                        """.trimIndent(),
                        variables = emptyMap()
                    )
                )
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.data.protectedWithParam").isEqualTo(1)
        }

        it("should protectedWithParam 2") {
            webTestClient.mutateWith(mockUser())
                .post()
                .uri("/graphql")
                .bodyValue(
                    GraphQLRequest(
                        query = """
                            query {
                                protectedWithParam(param: 2)
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

        it("should allow protectedWithCustomChecker 1") {
            webTestClient.mutateWith(mockUser())
                .post()
                .uri("/graphql")
                .bodyValue(
                    GraphQLRequest(
                        query = """
                            query {
                                protectedWithCustomChecker(param: 1)
                            }
                        """.trimIndent(),
                        variables = emptyMap()
                    )
                )
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.data.protectedWithCustomChecker").isEqualTo(1)
        }

        it("should protectedWithCustomChecker 2") {
            webTestClient.mutateWith(mockUser())
                .post()
                .uri("/graphql")
                .bodyValue(
                    GraphQLRequest(
                        query = """
                            query {
                                protectedWithCustomChecker(param: 2)
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
    }
})