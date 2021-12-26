package io.github.wickedev.spring.security

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import org.springframework.security.core.authority.SimpleGrantedAuthority

class DslRoleHierarchyTest : DescribeSpec({

    describe("DslRoleHierarchy") {
        it("should") {
            val hierarchy = DslRoleHierarchy {
                "ROLE_ADMIN" {
                    "ROLE_MANAGER" {
                        "ROLE_USER" {
                            +"ROLE_GUEST"
                        }
                    }
                }
            }

            val granted = hierarchy.getReachableGrantedAuthorities(listOf(SimpleGrantedAuthority("ROLE_MANAGER")))

            granted shouldNotContain SimpleGrantedAuthority("ROLE_ADMIN")
            granted shouldContain SimpleGrantedAuthority("ROLE_MANAGER")
            granted shouldContain SimpleGrantedAuthority("ROLE_USER")
            granted shouldContain SimpleGrantedAuthority("ROLE_GUEST")
        }
    }
})
