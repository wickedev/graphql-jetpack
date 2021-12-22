package io.github.wickedev.graphql.security

class DslRoleHierarchy(
    spec: RoleHierarchyDsl.() -> Unit
) : MapRoleHierarchy(spec.let { it ->
    val dsl = RoleHierarchyDsl()
    it(dsl)
    return@let dsl.build()
}) {
    class RoleHierarchyDsl(private val role: String? = null) {
        private val children = mutableListOf<Pair<String, List<String>>>()
        private val roles = mutableListOf<String>()

        fun build(): Map<String, List<String>> {
            return mapOf(*children.toTypedArray())
        }

        private fun current(): List<Pair<String, List<String>>> {
            return role?.let { listOf(it to roles, *children.toTypedArray()) } ?: children
        }

        operator fun String.invoke(spec: RoleHierarchyDsl.() -> Unit) {
            roles.add(this)

            val child = RoleHierarchyDsl(this)
            spec(child)
            children.addAll(child.current())
        }

        operator fun String.unaryPlus() {
            roles.add(this)
        }
    }
}
