package io.github.wickedev.graphql.security

import org.springframework.security.core.userdetails.UserDetails

interface GraphQLUser : UserDetails {
    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}