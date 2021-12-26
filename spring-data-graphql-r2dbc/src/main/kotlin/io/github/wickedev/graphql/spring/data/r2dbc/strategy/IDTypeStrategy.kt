package io.github.wickedev.graphql.spring.data.r2dbc.strategy

import java.lang.reflect.Field
import kotlin.reflect.KClass

interface IDTypeStrategy {
    fun convertType(type: Class<*>, tableName: String?): String

    fun convertType(entity: Class<*>, property: Field, globalType: String?, relationType: KClass<*>?): String
}