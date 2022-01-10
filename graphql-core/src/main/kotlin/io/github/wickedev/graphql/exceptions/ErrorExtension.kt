package io.github.wickedev.graphql.exceptions

data class ErrorExtension(
    val code: String,
    val exception: Exception
)