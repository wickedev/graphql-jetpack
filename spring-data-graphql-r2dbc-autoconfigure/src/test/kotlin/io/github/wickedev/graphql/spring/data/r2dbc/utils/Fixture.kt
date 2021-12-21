package io.github.wickedev.graphql.spring.data.r2dbc.utils

import com.appmattus.kotlinfixture.decorator.fake.javafaker.javaFakerStrategy
import com.appmattus.kotlinfixture.kotlinFixture
import io.github.wickedev.graphql.types.ID

val fixture = kotlinFixture {
    javaFakerStrategy {
        factory<ID> { ID("") }
    }
}
