package io.github.wickedev.graphql.spring.data.r2dbc.strategy

import io.github.wickedev.graphql.types.ID

class GraphQLAdditionalIsNewStrategy : AdditionalIsNewStrategy {

    override fun isNew(type: Class<*>?, value: Any?): Boolean {
        return if (type == ID::class.java)
            (value as ID).value.isEmpty()
        else false
    }
}
