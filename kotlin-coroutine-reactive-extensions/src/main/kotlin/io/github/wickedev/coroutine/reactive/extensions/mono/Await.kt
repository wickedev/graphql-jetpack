package io.github.wickedev.coroutine.reactive.extensions.mono

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.suspendCancellableCoroutine
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import reactor.core.publisher.Mono
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend inline fun <reified T> Mono<T>.await(): T {
    if (null is T) {
        // for Mono<T?>
        return awaitFirstOrNull() as T
    }

    return when (T::class) {
        // for Mono<Unit> or Mono<Void>
        Unit::class, Void::class -> {
            @Suppress("UNCHECKED_CAST", "ReactiveStreamsSubscriberImplementation")
            suspendCancellableCoroutine { cont ->
                cont as CancellableContinuation<T?>
                subscribe(object : Subscriber<T?> {
                    override fun onSubscribe(s: Subscription) {
                        cont.invokeOnCancellation {
                            s.cancel()
                        }
                        s.request(1)
                    }

                    override fun onNext(t: T?) {
                        error("Mono<Void> or Mono<Unit> cannot emit value")
                    }

                    override fun onError(t: Throwable) {
                        cont.resumeWithException(t)
                    }

                    override fun onComplete() {
                        cont.resume(null)
                    }
                })
            }
        }
        // for Mono<T>
        else -> awaitFirst()
    }
}
