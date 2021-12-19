package io.github.wickedev.graphql.spring.data.r2dbc

import com.appmattus.kotlinfixture.decorator.fake.javafaker.javaFakerStrategy
import com.appmattus.kotlinfixture.kotlinFixture
import io.github.wickedev.graphql.scalars.ID

val fixture = kotlinFixture {
    javaFakerStrategy {
        factory<ID> { ID("") }
    }
}
