package io.github.wickedev.coroutine.reactive.extensions.flux

import kotlinx.coroutines.reactive.awaitSingle
import reactor.core.publisher.Flux

suspend fun <T> Flux<T>.await(): List<T> = this.collectList().awaitSingle()
