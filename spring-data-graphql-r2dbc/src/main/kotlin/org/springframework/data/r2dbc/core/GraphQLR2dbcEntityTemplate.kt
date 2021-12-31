package org.springframework.data.r2dbc.core

import io.github.wickedev.graphql.spring.data.r2dbc.strategy.IDTypeFiller
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.dialect.R2dbcDialect
import org.springframework.data.r2dbc.mapping.OutboundRow
import org.springframework.data.relational.core.sql.SqlIdentifier
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Mono

class GraphQLR2dbcEntityTemplate(
    databaseClient: DatabaseClient,
    dialect: R2dbcDialect,
    r2dbcConverter: R2dbcConverter
) : R2dbcEntityTemplate(databaseClient, dialect, r2dbcConverter) {

    private val mappingContext = dataAccessStrategy.converter.mappingContext
    private val idTypeFiller = IDTypeFiller(mappingContext)

    override fun <T : Any> maybeCallAfterSave(`object`: T, row: OutboundRow, table: SqlIdentifier): Mono<T> {
        return super.maybeCallAfterSave(idTypeFiller.setIDTypeIfNecessary(`object`), row, table)
    }

    override fun <T : Any> maybeCallAfterConvert(`object`: T, table: SqlIdentifier): Mono<T> {
        return super.maybeCallAfterConvert(idTypeFiller.setIDTypeIfNecessary(`object`), table)
    }
}