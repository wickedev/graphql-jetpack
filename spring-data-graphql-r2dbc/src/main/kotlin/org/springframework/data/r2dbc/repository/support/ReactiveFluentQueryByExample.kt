package org.springframework.data.r2dbc.repository.support

import io.github.wickedev.graphql.spring.data.r2dbc.repository.base.PropertyRepository
import org.springframework.data.domain.Example
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.ReactiveSelectOperation
import org.springframework.data.relational.core.query.Query
import org.springframework.util.Assert
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.function.UnaryOperator

internal class ReactiveFluentQueryByExample<S, T>(
    example: Example<S>,
    sort: Sort,
    resultType: Class<T>,
    fieldsToInclude: List<String>,
    private val propertyRepository: PropertyRepository<*, *>,
) :
    ReactiveFluentQuerySupport<Example<S>, T>(example, sort, resultType, fieldsToInclude) {


    constructor(
        example: Example<S>,
        resultType: Class<T>,
        propertyRepository: PropertyRepository<*, *>
    ) : this(
        example,
        Sort.unsorted(),
        resultType,
        emptyList<String>(),
        propertyRepository
    )

    override fun <R> create(
        predicate: Example<S>, sort: Sort, resultType: Class<R>,
        fieldsToInclude: List<String>,
    ): ReactiveFluentQueryByExample<S, R> {
        return ReactiveFluentQueryByExample(predicate, sort, resultType, fieldsToInclude, this.propertyRepository)
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.query.FluentQuery.ReactiveFluentQuery#one()
     */
    override fun one(): Mono<T> {
        return createQuery().one()
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.query.FluentQuery.ReactiveFluentQuery#first()
     */
    override fun first(): Mono<T> {
        return createQuery().first()
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.query.FluentQuery.ReactiveFluentQuery#all()
     */
    override fun all(): Flux<T> {
        return createQuery().all()
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.query.FluentQuery.ReactiveFluentQuery#page(org.springframework.data.domain.Pageable)
     */
    override fun page(pageable: Pageable): Mono<Page<T>> {
        Assert.notNull(pageable, "Pageable must not be null!")
        val items = createQuery { q: Query ->
            q.with(
                pageable
            )
        }.all().collectList()
        return items.flatMap { content: List<T>? ->
            ReactivePageableExecutionUtils.getPage(
                content,
                pageable,
                this.count()
            )
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.query.FluentQuery.ReactiveFluentQuery#count()
     */
    override fun count(): Mono<Long> {
        return createQuery().count()
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.query.FluentQuery.ReactiveFluentQuery#exists()
     */
    override fun exists(): Mono<Boolean> {
        return createQuery().exists()
    }

    private fun createQuery(queryCustomizer: UnaryOperator<Query> = UnaryOperator.identity()): ReactiveSelectOperation.TerminatingSelect<T> {
        var query: Query = propertyRepository.exampleMapper.getMappedExample(predicate)
        if (sort.isSorted) {
            query = query.sort(sort)
        }
        if (!fieldsToInclude.isEmpty()) {
            query = query.columns(*fieldsToInclude.toTypedArray())
        }
        query = queryCustomizer.apply(query)
        val select: ReactiveSelectOperation.ReactiveSelect<S> =
            propertyRepository.entityOperations.select(predicate.probeType)
        return if (resultType != predicate.probeType) {
            select.`as`(resultType).matching(query)
        } else select.matching(query) as ReactiveSelectOperation.TerminatingSelect<T>
    }
}