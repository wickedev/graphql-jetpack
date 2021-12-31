package io.github.wickedev.graphql.spring.data.r2dbc.strategy

import io.github.wickedev.graphql.spring.data.r2dbc.mapping.GraphQLTypeInformation
import io.github.wickedev.graphql.types.ID
import org.springframework.data.mapping.PersistentPropertyAccessor
import org.springframework.data.mapping.context.MappingContext
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty

class IDTypeFiller(private val mappingContext: MappingContext<out RelationalPersistentEntity<*>, out RelationalPersistentProperty>) {

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> setIDTypeIfNecessary(`object`: T): T {
        var updated = false
        val entity = mappingContext.getRequiredPersistentEntity(`object`.javaClass)
        val propertyAccessor: PersistentPropertyAccessor<*> = entity.getPropertyAccessor(`object`)

        for (property in entity) {
            val value = propertyAccessor.getProperty(property)
            val type = property.typeInformation

            if (value is ID && value.type.isEmpty() && type is GraphQLTypeInformation<*>) {
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