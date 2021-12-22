package io.github.wickedev.graphql.security

import io.kotest.core.spec.style.DescribeSpec
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient
class AuthTest(
    private val webTestClient: WebTestClient
) : DescribeSpec({

    describe("AuthTest") {
        it("should login") {
            webTestClient.post()
                .uri("/login")
                .bodyValue(AuthRequest("email@email.com", "password"))
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(System.out::println)
        }

        it("should allow by user") {
            webTestClient
                .mutateWith(mockUser().roles("USER"))
                .get()
                .uri("/allow-user")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(System.out::println)
        }

        it("should reject by user") {
            webTestClient
                .mutateWith(mockUser().roles("USER"))
                .get()
                .uri("/allow-admin")
                .exchange()
                .expectStatus().isForbidden
                .expectBody()
                .consumeWith(System.out::println)
        }
    }
})
