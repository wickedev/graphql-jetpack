package io.github.wickedev.graphql.spring.data.r2dbc.strategy

import io.github.wickedev.graphql.spring.data.r2dbc.extentions.camelToSnakeCase
import java.lang.reflect.Field
import kotlin.reflect.KClass

class DefaultIDTypeStrategy : IDTypeStrategy {
    override fun convertType(type: Class<*>, tableName: String?): String {
        return type.simpleName.camelToSnakeCase()
    }

    override fun convertType(entity: Class<*>, property: Field, globalType: String?, relationType: KClass<*>?): String {
        if (globalType != null && relationType != null) {
            throw IllegalStateException("ID type property cannot declare both of @Relation and @GlobalId annotation on ${entity.name}::${property.name}")
        }

        return globalType ?: relationType?.simpleName?.camelToSnakeCase() ?: ""
    }
}