package io.github.wickedev.graphql.spring.data.r2dbc.repository.mixin

import io.github.wickedev.graphql.spring.data.r2dbc.repository.base.PropertyRepository
import org.springframework.data.domain.Sort
import org.springframework.data.relational.core.query.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.util.Assert
import reactor.core.publisher.Flux

@NoRepositoryBean
interface ReactiveSortingRepositoryMixin<T, ID> :
    ReactiveSortingRepository<T, ID>,
    ReactiveCrudRepositoryMixin<T, ID>,
    PropertyRepository<T, ID> {

    /* (non-Javadoc)
	 * @see org.springframework.data.repository.reactive.ReactiveSortingRepository#findAll(org.springframework.data.domain.Sort)
	 */
    override fun findAll(sort: Sort): Flux<T> {
        Assert.notNull(sort, "Sort must not be null!")
        return entityOperations.select(Query.empty().sort(sort), entity.javaType)
    }

}