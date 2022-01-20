package io.github.wickedev.graphql.enums

import graphql.schema.GraphQLEnumType

val REQUIRED_FIELD_ACTION = GraphQLEnumType.newEnum()
    .name("RequiredFieldAction")
    .value("NONE")
    .value("LOG")
    .value("THROW")
    .build()