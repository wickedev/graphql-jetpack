package io.github.wickedev.graphql.security

import org.springframework.security.access.ConfigAttribute

class AuthDirectiveConfigAttribute(private val value: String) : ConfigAttribute {
    override fun getAttribute(): String = value
}