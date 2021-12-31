@file:Suppress("DEPRECATION")

package io.github.wickedev.graphql.spring.data.r2dbc.query

import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy
import org.springframework.data.r2dbc.repository.query.R2dbcQueryMethod
import org.springframework.data.repository.query.QueryMethod
import org.springframework.data.repository.query.RepositoryQuery

class GraphQLEntityPartTreeR2dbcQuery(
    method: R2dbcQueryMethod,
    entityOperations: R2dbcEntityOperations,
    converter: R2dbcConverter,
    dataAccessStrategy: ReactiveDataAccessStrategy
) : RepositoryQuery {
    override fun execute(parameters: Array<out Any>): Any? {
        TODO("Not yet implemented")
    }

    override fun getQueryMethod(): QueryMethod {
        TODO("Not yet implemented")
    }
}