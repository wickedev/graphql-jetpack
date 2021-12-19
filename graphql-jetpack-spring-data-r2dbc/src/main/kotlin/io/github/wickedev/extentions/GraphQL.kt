package io.github.wickedev.extentions

import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory

fun <K, V> DataFetchingEnvironment.dataLoader(
    key: String,
    block: suspend (keys: List<K>) -> List<V>
): DataLoader<K, V> {
    return dataLoaderRegistry.computeIfAbsent(key) {
        DataLoaderFactory.newDataLoader<K, V> { keys ->
            CoroutineScope(Dispatchers.Unconfined).future {
                block(keys)
            }
        }
    }
}
