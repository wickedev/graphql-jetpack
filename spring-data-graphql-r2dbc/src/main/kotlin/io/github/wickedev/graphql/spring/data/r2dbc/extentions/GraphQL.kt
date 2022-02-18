package io.github.wickedev.graphql.spring.data.r2dbc.extentions

import graphql.schema.DataFetchingEnvironment
import io.github.wickedev.graphql.types.Direction
import io.github.wickedev.graphql.types.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import org.springframework.data.domain.Sort

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

fun Order.toSpringDataType(): Sort.Order {
    return when (direction) {
        Direction.DESC -> Sort.Order.desc(property)
        Direction.ASC -> Sort.Order.asc(property)
        null -> Sort.Order.by(property)
    }
}

fun List<Order>?.toSpringDataType(order: Sort.Order? = null): Sort {
    return Sort.by(listOfNotNull(order) + (this?.map {
        it.toSpringDataType()
    }?: emptyList()))
}