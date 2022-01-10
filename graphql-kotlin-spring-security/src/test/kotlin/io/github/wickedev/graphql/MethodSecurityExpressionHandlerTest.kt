package io.github.wickedev.graphql

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.expression.EvaluationContext
import org.springframework.security.access.expression.ExpressionUtils
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.util.MethodInvocationUtils

@SpringBootTest
class MethodSecurityExpressionHandlerTest(
    val expressionHandler: MethodSecurityExpressionHandler,
) : DescribeSpec({
    describe("MethodSecurityExpressionHandler") {
        it("should expression evaluate true") {
            val controller = AnnotatedController()
            val parser = expressionHandler.expressionParser
            val expression = parser.parseExpression("hasRole('USER')")
            val authorities = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
            val authentication: Authentication = AnonymousAuthenticationToken("key", "anonymous", authorities)

            val mi = MethodInvocationUtils.createFromClass(
                controller,
                AnnotatedController::class.java,
                AnnotatedController::preAuthorize.name,
                emptyArray(),
                emptyArray()
            )

            val ctx: EvaluationContext = expressionHandler.createEvaluationContext(authentication, mi)

            val result = ExpressionUtils.evaluateAsBoolean(expression, ctx)
            result shouldBe false
        }
    }
})
