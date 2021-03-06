package io.github.wickedev.graphql

import com.expediagroup.graphql.server.spring.execution.SpringDataFetcher
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.extentions.genericType
import io.github.wickedev.graphql.scalars.CustomScalars
import org.aopalliance.intercept.MethodInvocation
import org.springframework.context.ApplicationContext
import org.springframework.expression.EvaluationContext
import org.springframework.security.access.expression.ExpressionUtils
import org.springframework.security.access.expression.SecurityExpressionHandler
import org.springframework.security.core.Authentication
import org.springframework.security.util.MethodInvocationUtils
import reactor.core.publisher.Mono
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure

open class JetpackFunctionDataFetcher(
    private val target: Any?,
    private val fn: KFunction<*>,
    objectMapper: ObjectMapper,
    applicationContext: ApplicationContext,
    private val securityExpressionHandler: SecurityExpressionHandler<MethodInvocation>,
    private val customScalars: CustomScalars,
) : SpringDataFetcher(target, fn, objectMapper, applicationContext) {

    override fun get(environment: DataFetchingEnvironment): Any? {
        return when (val result = super.get(environment)) {
            is Mono<*> -> result.toFuture()
            else -> result
        }
    }

    @ExperimentalStdlibApi
    override fun mapParameterToValue(param: KParameter, environment: DataFetchingEnvironment): Pair<KParameter, Any?>? {
        return when (true) {
            param.type.classifier == Authentication::class -> param to environment.graphQlContext.authentication
            customScalars.exists(param.type), customScalars.exists(param.genericType) -> param to environment.arguments[param.name]
            else -> super.mapParameterToValue(param, environment)
        }
    }


    fun check(authentication: Authentication?, requires: String, environment: DataFetchingEnvironment): Boolean {
        val instance: Any? = target ?: environment.getSource<Any?>()
        val parameterValues = getParameters(fn, environment)

        val expression = securityExpressionHandler.expressionParser.parseExpression(requires)
        val typeParameters: List<Class<*>> = fn.valueParameters.map { it.type.jvmErasure.java }
        val mi = MethodInvocationUtils.createFromClass(
            instance,
            instance?.javaClass,
            fn.name,
            typeParameters.toTypedArray(),
            parameterValues.values.toTypedArray()
        )

        val ctx: EvaluationContext = securityExpressionHandler.createEvaluationContext(authentication, mi)
        return ExpressionUtils.evaluateAsBoolean(expression, ctx)
    }
}