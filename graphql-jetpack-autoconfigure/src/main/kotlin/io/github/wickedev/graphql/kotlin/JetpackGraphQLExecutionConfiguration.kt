package io.github.wickedev.graphql.kotlin

import com.expediagroup.graphql.generator.execution.KotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.server.spring.GraphQLExecutionConfiguration
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.wickedev.graphql.scalars.CustomScalars
import io.github.wickedev.graphql.security.CustomDefaultMethodSecurityExpressionHandler
import org.aopalliance.intercept.MethodInvocation
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.access.expression.SecurityExpressionHandler
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import java.util.*

@Configuration
@AutoConfigureBefore(GraphQLExecutionConfiguration::class)
class JetpackGraphQLExecutionConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun dataFetcherFactoryProvider(
        objectMapper: ObjectMapper, applicationContext: ApplicationContext,
        roleHierarchy: Optional<RoleHierarchy>,
        securityExpressionHandler: SecurityExpressionHandler<MethodInvocation>,
        customScalars: CustomScalars,
    ): KotlinDataFetcherFactoryProvider =
        JetpackDataFetcherFactoryProvider(
            objectMapper,
            applicationContext,
            securityExpressionHandler,
            customScalars
        )

    @Bean
    @Primary
    @ConditionalOnMissingBean
    fun getSecurityExpressionHandler(roleHierarchy: Optional<RoleHierarchy>): CustomDefaultMethodSecurityExpressionHandler {
        val handler = CustomDefaultMethodSecurityExpressionHandler()
        roleHierarchy.ifPresent {
            handler.setRoleHierarchy(it)
        }

        return handler
    }
}