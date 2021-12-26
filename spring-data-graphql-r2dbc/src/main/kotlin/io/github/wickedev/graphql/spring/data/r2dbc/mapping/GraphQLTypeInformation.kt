package io.github.wickedev.graphql.spring.data.r2dbc.mapping

import io.github.wickedev.graphql.annotations.GlobalId
import io.github.wickedev.graphql.annotations.Relation
import io.github.wickedev.graphql.spring.data.r2dbc.strategy.IDTypeStrategy
import io.github.wickedev.graphql.types.ID
import org.springframework.data.mapping.PersistentEntity
import org.springframework.data.mapping.PersistentProperty
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.util.TypeInformation

class GraphQLTypeInformation<T>(
    private val property: PersistentProperty<*>,
    private val owner: PersistentEntity<*, RelationalPersistentProperty?>,
    private val idTypeStrategy: IDTypeStrategy,
    information: TypeInformation<T>
) : TypeInformation<T> by information {
    fun isGraphQLID(): Boolean {
        return type == ID::class.java
    }

    fun typeName(): String {
        if (owner.isIdProperty(property)) {
            val table = owner.findAnnotation(Table::class.java)
            return idTypeStrategy.convertType(owner.type, table?.value ?: owner.type.simpleName)
        }

        val gid = property.findAnnotation(GlobalId::class.java)
        val rid = property.findAnnotation(Relation::class.java)

        return idTypeStrategy.convertType(owner.type, property.requiredField, gid?.type, rid?.type)
    }
}