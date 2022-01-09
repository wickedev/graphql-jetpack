package io.github.wickedev.spring.security

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.expression.BeanFactoryResolver
import org.springframework.expression.EvaluationContext
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.security.access.expression.ExpressionUtils
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.util.MethodInvocationUtils

@SpringBootTest
class ASDF(
    val expressionHandler: MethodSecurityExpressionHandler,
    val applicationContext: ApplicationContext
) : DescribeSpec({
    describe("ASDF") {
        it("should protect preAuthorize method") {
            val controller = AnnotatedController()
            val parser = SpelExpressionParser()
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


            /*val ctx: EvaluationContext = StandardEvaluationContext().apply {
                setRootObject(GraphQLSecurityExpressionRoot(authentication))
                beanResolver = BeanFactoryResolver(applicationContext)
            }*/

            val ctx: EvaluationContext = expressionHandler.createEvaluationContext(authentication, mi)

            val result = ExpressionUtils.evaluateAsBoolean(expression, ctx)
            result shouldBe false
        }
    }
})
