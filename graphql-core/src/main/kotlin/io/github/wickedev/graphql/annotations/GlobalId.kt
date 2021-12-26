package io.github.wickedev.graphql.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class GlobalId(val type: String)