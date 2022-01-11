package io.github.wickedev.spring.security.jwt

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.util.Assert

class JwtAuthenticationToken(private val jwt: JWT) : Authentication {
    override fun getName(): String = jwt.subject

    override fun getAuthorities(): Collection<GrantedAuthority> = jwt.authorities

    override fun getCredentials(): String? = null

    override fun getDetails(): JWT = jwt

    override fun getPrincipal(): JWT = jwt

    override fun isAuthenticated(): Boolean = true

    @Throws(IllegalArgumentException::class)
    override fun setAuthenticated(isAuthenticated: Boolean) {
        Assert.isTrue(
            isAuthenticated,
            "Cannot set this token to untrusted - use constructor which takes a Claims instead"
        )
    }
}