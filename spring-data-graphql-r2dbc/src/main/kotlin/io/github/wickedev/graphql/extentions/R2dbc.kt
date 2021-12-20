package io.github.wickedev.graphql.extentions

import org.springframework.core.annotation.AnnotationUtils
import org.springframework.data.domain.Sort
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.core.RepositoryMetadata

val Sort.Direction.inverted: Sort.Direction
    get() = if (isAscending) Sort.Direction.DESC else Sort.Direction.ASC

val RepositoryMetadata.name: String
    get() = tableName ?: this.domainType.simpleName.lowercase()

val RepositoryMetadata.tableName: String?
    get() = AnnotationUtils.findAnnotation(
        this.domainType,
        Table::class.java
    )?.value