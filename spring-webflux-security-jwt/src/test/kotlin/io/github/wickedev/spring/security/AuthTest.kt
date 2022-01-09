package io.github.wickedev.spring.security

import io.kotest.core.spec.style.DescribeSpec
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Hooks
import java.time.Duration

@SpringBootTest
@AutoConfigureWebTestClient
class AuthTest(
    private val webTestClient: WebTestClient
) : DescribeSpec({

    describe("AuthTest") {
        it("should allow as public") {
            webTestClient.get()
                .uri("/allow")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(System.out::println)
        }

        it("should allow to user") {
            webTestClient
                .mutateWith(mockUser().roles("USER"))
                .get()
                .uri("/protect/user")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(System.out::println)
        }

        it("should reject to user") {
            webTestClient
                .mutateWith(mockUser().roles("USER"))
                .get()
                .uri("/protect/admin")
                .exchange()
                .expectStatus().isForbidden
                .expectBody()
                .consumeWith(System.out::println)
        }

        it("secured annotated end point should allow to user") {
            webTestClient
                .mutateWith(mockUser().roles("USER"))
                .get()
                .uri("/protect/secured")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(System.out::println)
        }

        it("pre-authorize annotated end point should not allow to anonymous") {
            Hooks.onOperatorDebug()
            webTestClient
                .mutate()
                .responseTimeout(Duration.ofDays(1))
                .build()
                .get()
                .uri("/protect/pre-authorize")
                .exchange()
                .expectStatus().isUnauthorized
                .expectBody()
                .consumeWith(System.out::println)
        }
    }
})
