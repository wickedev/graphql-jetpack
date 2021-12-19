package io.github.wickedev.graphql.spring.data.r2dbc.utils

import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

fun DataFetchingEnvironment.dispatchAll() = CoroutineScope(Dispatchers.Unconfined).launch {
    dataLoaderRegistry.dataLoaders.forEach {
        it.dispatch()
    }
}

suspend fun <T> CompletableFuture<T>.dispatchThenAwait(env: DataFetchingEnvironment): T {
    env.dispatchAll()
    return this.await()
}