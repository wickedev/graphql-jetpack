package io.github.wickedev.spring.security

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import org.springframework.security.core.authority.SimpleGrantedAuthority

class MapRoleHierarchyTest : DescribeSpec({

    describe("MapRoleHierarchy") {
        it("should") {
            val hierarchy = MapRoleHierarchy(
                mapOf(
                    "ROLE_ADMIN" to listOf(
                        "ROLE_MANAGER"
                    ),
                    "ROLE_MANAGER" to listOf(
                        "ROLE_USER"
                    ),
                    "ROLE_USER" to listOf(
                        "ROLE_GUEST"
                    )
                )
            )

            val granted = hierarchy.getReachableGrantedAuthorities(listOf(SimpleGrantedAuthority("ROLE_MANAGER")))

            granted shouldNotContain SimpleGrantedAuthority("ROLE_ADMIN")
            granted shouldContain SimpleGrantedAuthority("ROLE_MANAGER")
            granted shouldContain SimpleGrantedAuthority("ROLE_USER")
            granted shouldContain SimpleGrantedAuthority("ROLE_GUEST")
        }
    }
})
