package io.github.wickedev.graphql.spring.data.r2dbc.converter

import io.github.wickedev.graphql.spring.data.r2dbc.mapping.GraphQLTypeInformation
import io.github.wickedev.graphql.spring.data.r2dbc.strategy.AdditionalIsNewStrategy
import io.github.wickedev.graphql.types.ID
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.data.convert.CustomConversions
import org.springframework.data.mapping.PersistentPropertyAccessor
import org.springframework.data.mapping.callback.ReactiveEntityCallbacks
import org.springframework.data.mapping.context.MappingContext
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty
import org.springframework.data.relational.core.sql.SqlIdentifier
import org.springframework.data.util.TypeInformation
import org.springframework.util.ClassUtils
import reactor.core.publisher.Mono
import java.util.function.BiFunction

class GraphQLMappingR2dbcConverter(
    context: MappingContext<out RelationalPersistentEntity<*>?, out RelationalPersistentProperty?>,
    conversions: CustomConversions,
    private val additionalIsNewStrategy: AdditionalIsNewStrategy,
) : MappingR2dbcConverter(context, conversions), ApplicationContextAware {

    private var entityCallbacks: ReactiveEntityCallbacks? = null

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        if (entityCallbacks == null) {
            entityCallbacks = ReactiveEntityCallbacks.create(applicationContext)
        }
    }

    override fun <T : Any?> populateIdIfNecessary(obj: T): BiFunction<Row, RowMetadata, T?> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        val userClass = ClassUtils.getUserClass(obj)
        val entity = mappingContext.getRequiredPersistentEntity(userClass)

        return BiFunction { row: Row, metadata: RowMetadata ->
            val propertyAccessor: PersistentPropertyAccessor<*> = entity.getPropertyAccessor(obj)
            val tableName = entity.tableName
            val idProperty = entity.requiredIdProperty
            val id = propertyAccessor.getProperty(idProperty)

            val idPropertyUpdateNeeded = when {
                additionalIsNewStrategy.isNew(idProperty.type, id) -> true
                idProperty.type.isPrimitive && id is Number && id == 0L -> true
                idProperty.type.isPrimitive && id is String && id == "" -> true
                else -> id == null
            }

            @Suppress("UNCHECKED_CAST")
            if (idPropertyUpdateNeeded) {
                val `object`: T = if (potentiallySetId(
                        row,
                        metadata,
                        propertyAccessor,
                        idProperty,
                    )
                ) propertyAccessor.bean as T else obj

                return@BiFunction maybeCallAfterConvert(`object`, tableName).toFuture().get()
            }
            obj
        }
    }

    private fun potentiallySetId(
        row: Row,
        metadata: RowMetadata,
        propertyAccessor: PersistentPropertyAccessor<*>,
        idProperty: RelationalPersistentProperty
    ): Boolean {
        val columns = metadata.columnNames
        var generatedIdValue: Any? = null
        val idColumnName = idProperty.columnName.reference
        if (columns.contains(idColumnName)) {
            generatedIdValue = row[idColumnName]
        } else if (columns.size == 1) {
            val key = columns.iterator().next()
            generatedIdValue = row[key]
        }
        if (generatedIdValue == null) {
            return false
        }

        propertyAccessor.setProperty(idProperty, readValue(generatedIdValue, idProperty.typeInformation))

        return true
    }

    private fun <T> maybeCallAfterConvert(`object`: T, table: SqlIdentifier?): Mono<T> {
        return entityCallbacks?.callback(AfterConvertCallback::class.java, `object`, table) ?: Mono.just(`object`)
    }

    override fun readValue(value: Any?, type: TypeInformation<*>): Any? {
        if (type is GraphQLTypeInformation<*> && type.isGraphQLID()) {
            return convertToId(type, value)
        }

        return super.readValue(value, type)
    }

    private fun <T> convertToId(type: GraphQLTypeInformation<T>, value: Any?): ID? {
        return value?.let { ID(type.typeName(), value.toString()) }
    }
}
