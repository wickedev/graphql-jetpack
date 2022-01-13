package io.github.wickedev.graphql.spring.data.r2dbc.mapping

import io.github.wickedev.graphql.spring.data.r2dbc.extentions.isAssignableFrom
import io.github.wickedev.graphql.spring.data.r2dbc.strategy.IDTypeStrategy
import org.springframework.data.mapping.PersistentEntity
import org.springframework.data.mapping.model.Property
import org.springframework.data.mapping.model.SimpleTypeHolder
import org.springframework.data.relational.core.mapping.BasicRelationalPersistentProperty
import org.springframework.data.relational.core.mapping.NamingStrategy
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty
import org.springframework.data.util.TypeInformation
import kotlin.properties.ReadOnlyProperty

class GraphQLRelationalPersistentProperty(
    property: Property,
    owner: PersistentEntity<*, RelationalPersistentProperty?>,
    simpleTypeHolder: SimpleTypeHolder,
    namingStrategy: NamingStrategy,
    private val idTypeStrategy: IDTypeStrategy,
) : BasicRelationalPersistentProperty(
    property, owner, simpleTypeHolder, namingStrategy
) {
    override fun getTypeInformation(): TypeInformation<*> {
        return GraphQLTypeInformation(this, owner, idTypeStrategy, super.getTypeInformation())
    }

    override fun isTransient(): Boolean {
        return super.isTransient() || ReadOnlyProperty::class.isAssignableFrom(this.type)
                || Lazy::class.isAssignableFrom(this.type)
    }
}

