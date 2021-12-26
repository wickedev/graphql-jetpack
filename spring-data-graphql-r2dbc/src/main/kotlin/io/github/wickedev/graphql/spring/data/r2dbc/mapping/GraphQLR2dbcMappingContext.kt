package io.github.wickedev.graphql.spring.data.r2dbc.mapping

import io.github.wickedev.graphql.spring.data.r2dbc.strategy.IDTypeStrategy
import org.springframework.data.mapping.model.Property
import org.springframework.data.mapping.model.SimpleTypeHolder
import org.springframework.data.r2dbc.mapping.R2dbcMappingContext
import org.springframework.data.relational.core.mapping.NamingStrategy
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty

class GraphQLR2dbcMappingContext(
    namingStrategy: NamingStrategy,
    private val idTypeStrategy: IDTypeStrategy,
): R2dbcMappingContext(namingStrategy) {

    override fun createPersistentProperty(
        property: Property,
        owner: RelationalPersistentEntity<*>,
        simpleTypeHolder: SimpleTypeHolder
    ): RelationalPersistentProperty {

        val persistentProperty = GraphQLRelationalPersistentProperty(
            property, owner,
            simpleTypeHolder, namingStrategy,
            idTypeStrategy,
        )
        persistentProperty.isForceQuote = isForceQuote

        return persistentProperty
    }
}