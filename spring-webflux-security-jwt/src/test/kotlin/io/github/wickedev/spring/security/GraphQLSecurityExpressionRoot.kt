package io.github.wickedev.spring.security

import org.springframework.security.access.expression.SecurityExpressionRoot
import org.springframework.security.core.Authentication

class GraphQLSecurityExpressionRoot(authentication: Authentication) : SecurityExpressionRoot(authentication)