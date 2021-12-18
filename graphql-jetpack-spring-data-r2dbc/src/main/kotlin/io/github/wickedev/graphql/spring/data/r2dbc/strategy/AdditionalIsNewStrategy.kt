package io.github.wickedev.graphql.spring.data.r2dbc.strategy

interface AdditionalIsNewStrategy {
    fun isNew(type: Class<*>?, value: Any?): Boolean
}