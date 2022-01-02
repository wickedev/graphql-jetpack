package io.github.wickedev.spring.security

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

    xdescribe("AuthTest") {
        xit("should allow as public") {
            webTestClient.post()
                .uri("/public")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(System.out::println)
        }

        xit("should allow to user") {
            webTestClient
                // .mutateWith(mockUser().roles("USER"))
                .get()
                .uri("/allow-user")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(System.out::println)
        }

        xit("should reject to user") {
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
