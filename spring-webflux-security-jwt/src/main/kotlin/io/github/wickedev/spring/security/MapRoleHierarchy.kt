package io.github.wickedev.spring.security

import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.access.hierarchicalroles.RoleHierarchyUtils

open class MapRoleHierarchy(roleHierarchyMap: Map<String, List<String>>) : RoleHierarchyImpl() {

    init {
        setHierarchy(RoleHierarchyUtils.roleHierarchyFromMap(roleHierarchyMap))
    }

    final override fun setHierarchy(roleHierarchyStringRepresentation: String) {
        super.setHierarchy(roleHierarchyStringRepresentation)
    }
}