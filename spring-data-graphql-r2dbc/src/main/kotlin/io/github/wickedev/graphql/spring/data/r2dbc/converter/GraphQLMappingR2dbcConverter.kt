package io.github.wickedev.graphql.spring.data.r2dbc.converter

import io.github.wickedev.graphql.spring.data.r2dbc.strategy.AdditionalIsNewStrategy
import io.r2dbc.spi.ColumnMetadata
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.data.convert.CustomConversions
import org.springframework.data.mapping.PersistentPropertyAccessor
import org.springframework.data.mapping.callback.ReactiveEntityCallbacks
import org.springframework.data.mapping.context.MappingContext
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty
import org.springframework.util.ClassUtils
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
                return@BiFunction if (potentiallySetId(
                        row,
                        metadata,
                        propertyAccessor,
                        idProperty,
                    )
                ) propertyAccessor.bean as T else obj
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
        val idColumnName = idProperty.columnName.reference
        val generatedIdValue = extractGeneratedIdentifier(row, metadata, idColumnName) ?: return false
        propertyAccessor.setProperty(idProperty, readValue(generatedIdValue, idProperty.typeInformation))
        return true
    }

    private fun extractGeneratedIdentifier(row: Row, metadata: RowMetadata, idColumnName: String): Any? {
        if (metadata.columnNames.contains(idColumnName)) {
            return row[idColumnName]
        }

        val columns = metadata.columnMetadatas
        val it: Iterator<ColumnMetadata> = columns.iterator()

        if (it.hasNext()) {
            val column = it.next()
            return row[column.name]
        }
        return null
    }
}
