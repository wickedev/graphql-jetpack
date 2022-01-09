package io.github.wickedev.spring.security

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.aop.framework.ProxyFactory
import org.springframework.aop.support.AopUtils
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.access.intercept.aopalliance.MethodSecurityMetadataSourceAdvisor
import reactor.core.publisher.Hooks
import reactor.test.StepVerifier

@SpringBootTest
class MethodSecurityMetadataSourceAdvisorTest(
    val advisor: MethodSecurityMetadataSourceAdvisor,
) : DescribeSpec({
    describe("MethodSecurityMetadataSourceAdvisor") {
        it("should protect preAuthorize method") {
            Hooks.onOperatorDebug()
            val controller = AnnotatedController()
            val canApply = AopUtils.canApply(advisor, AnnotatedController::class.java)
            canApply shouldBe true

            val pf = ProxyFactory()
            pf.addAdvisor(advisor)
            pf.setTarget(controller)

            val proxy = pf.proxy as AnnotatedController

            StepVerifier.create(proxy.preAuthorize())
                .verifyErrorMessage("Denied")
        }
    }
})
