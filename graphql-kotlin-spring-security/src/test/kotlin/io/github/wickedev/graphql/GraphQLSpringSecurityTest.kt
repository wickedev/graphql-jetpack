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

        it("protected should be protect") {
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

        it("protectedWithRole(non role user) should be protect") {
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

        it("protectedWithRole(user(ROLE_USER)) should be allow") {
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

        it("protectedWithRole(user(ROLE_ADMIN)) should be allow") {
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

        it("public query should be allow") {
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

        it("protectedWithParam(param is 1) should be allow") {
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

        it("protectedWithParam(param is 2) should be protect") {
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

        it("protectedWithCustomChecker(user name ryan and param is positive) should be allow") {
            webTestClient.mutateWith(mockUser("ryan"))
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

        it("protectedWithCustomChecker(param is 2) should be protect") {
            webTestClient.mutateWith(mockUser())
                .post()
                .uri("/graphql")
                .bodyValue(
                    GraphQLRequest(
                        query = """
                            query {
                                protectedWithCustomChecker(param: 0)
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