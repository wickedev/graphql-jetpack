package io.github.wickedev.spring.security

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration

@SpringBootTest
class JwtPropertiesTest(
    private val jwtProperties: JwtProperties
) : DescribeSpec({
    describe("JwtProperties") {
        it("should be") {
            jwtProperties.algorithm shouldBe "EC"
            jwtProperties.privateKeyPath shouldBe "classpath:keys/ec256-private.pem"
            jwtProperties.publicKeyPath shouldBe "classpath:keys/ec256-public.pem"
            jwtProperties.accessTokenExpiresIn shouldBe Duration.ofHours(1)
            jwtProperties.refreshTokenExpiresIn shouldBe Duration.ofDays(60)
        }
    }
})
