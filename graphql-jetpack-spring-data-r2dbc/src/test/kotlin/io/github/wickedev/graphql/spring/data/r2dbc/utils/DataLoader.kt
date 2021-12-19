package io.github.wickedev.graphql.spring.data.r2dbc.utils

import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun DataFetchingEnvironment.dispatchAll() = CoroutineScope(Dispatchers.Unconfined).launch {
    dataLoaderRegistry.dataLoaders.forEach {
        it.dispatch()
    }
}