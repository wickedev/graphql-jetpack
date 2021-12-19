package org.springframework.data.r2dbc.core

import io.github.wickedev.graphql.spring.data.r2dbc.repository.base.PropertyRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.util.ClassTypeInformation
import reactor.core.publisher.Flux

@NoRepositoryBean
interface R2dbcAllInQueryMixin<T, ID> : PropertyRepository<T, ID> {

    fun findAllByIdsIn(ids: List<ID>): Flux<T> {
        return entityOperations.select(getIdsInQuery(ids), entity.javaType)
    }

    fun findAllIdByIdsIn(ids: List<ID>): Flux<Long> {
        val selectSpec = statementMapper.createSelect(entity.tableName)
            .withCriteria(whereId().`in`(ids))
            .withProjection(getIdProperty().columnName)
        val operation = statementMapper.getMappedObject(selectSpec)
        val idTypeInfo = ClassTypeInformation.from(Long::class.java)
        return databaseClient.sql(operation)
            .map { row -> converter.readValue(row, idTypeInfo) as Long }.all()
    }
}