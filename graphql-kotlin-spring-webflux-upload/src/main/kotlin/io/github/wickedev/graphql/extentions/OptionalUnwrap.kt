package io.github.wickedev.graphql.extentions

import java.util.*

fun <T> Optional<T>.unwrap(): T? = orElse(null)