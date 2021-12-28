package org.springframework.data.r2dbc.core

import io.github.wickedev.graphql.spring.data.r2dbc.mapping.GraphQLTypeInformation
import io.github.wickedev.graphql.types.ID
import org.springframework.data.mapping.PersistentPropertyAccessor
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

    override fun <T : Any> maybeCallAfterSave(`object`: T, row: OutboundRow, table: SqlIdentifier): Mono<T> {
        return super.maybeCallAfterSave(setIDTypeIfNecessary(`object`), row, table)
    }

    override fun <T : Any> maybeCallAfterConvert(`object`: T, table: SqlIdentifier): Mono<T> {
        return super.maybeCallAfterConvert(setIDTypeIfNecessary(`object`), table)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> setIDTypeIfNecessary(`object`: T): T {
        var updated = false
        val entity = mappingContext.getRequiredPersistentEntity(`object`.javaClass)
        val propertyAccessor: PersistentPropertyAccessor<*> = entity.getPropertyAccessor(`object`)

        for (property in entity) {
            val value = propertyAccessor.getProperty(property)
            val type = property.typeInformation

            if (type is GraphQLTypeInformation<*> && value is ID && value.type.isEmpty()) {
                propertyAccessor.setProperty(property, convertToId(type, value.value))
                updated = true
            }
        }

        return if (updated) propertyAccessor.bean as T else `object`
    }

    private fun <T> convertToId(type: GraphQLTypeInformation<T>, value: Any?): ID? {
        return value?.let { ID(type.typeName(), value.toString()) }
    }
}