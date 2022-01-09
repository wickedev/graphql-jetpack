package io.github.wickedev.graphql.kotlin

import com.expediagroup.graphql.generator.execution.SimpleKotlinDataFetcherFactoryProvider
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetcherFactory
import io.github.wickedev.graphql.JetpackFunctionDataFetcher
import io.github.wickedev.graphql.scalars.CustomScalars
import org.aopalliance.intercept.MethodInvocation
import org.springframework.context.ApplicationContext
import org.springframework.security.access.expression.SecurityExpressionHandler
import kotlin.reflect.KFunction

open class JetpackDataFetcherFactoryProvider(
    private val objectMapper: ObjectMapper,
    private val applicationContext: ApplicationContext,
    private val securityExpressionHandler: SecurityExpressionHandler<MethodInvocation>,
    private val customScalars: CustomScalars
) : SimpleKotlinDataFetcherFactoryProvider(objectMapper) {

    override fun functionDataFetcherFactory(target: Any?, kFunction: KFunction<*>): DataFetcherFactory<Any?> =
        DataFetcherFactory {
            JetpackFunctionDataFetcher(
                target,
                kFunction,
                objectMapper,
                applicationContext,
                securityExpressionHandler,
                customScalars
            )
        }
}