package io.github.wickedev.graphql.kotlin

import com.expediagroup.graphql.server.spring.execution.SpringGraphQLContext
import com.expediagroup.graphql.server.spring.execution.SpringGraphQLContextFactory
import io.github.wickedev.coroutine.reactive.extensions.mono.await
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.reactive.function.server.ServerRequest

open class JetpackSpringGraphQLContextFactory : SpringGraphQLContextFactory<SpringGraphQLContext>() {

    @Suppress("OverridingDeprecatedMember")
    override suspend fun generateContext(request: ServerRequest): SpringGraphQLContext? = null

    override suspend fun generateContextMap(request: ServerRequest): Map<*, Any> {

        val security = ReactiveSecurityContextHolder.getContext().await()
        val authentication = security?.authentication

        return buildMap {
            put("request", request)
            if (authentication != null) put("authentication", authentication)
        }
    }
}
