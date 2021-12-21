package io.github.wickedev.graphql.spring.data.r2dbc.repository.mixin

import io.github.wickedev.graphql.spring.data.r2dbc.repository.base.PropertyRepository
import org.reactivestreams.Publisher
import org.springframework.data.domain.Example
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.repository.support.ReactiveFluentQueryByExample
import org.springframework.data.relational.core.query.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.query.FluentQuery
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor
import org.springframework.util.Assert
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.function.Function

@NoRepositoryBean
interface ReactiveQueryByExampleExecutorMixin<T, ID> : ReactiveQueryByExampleExecutor<T>, PropertyRepository<T, ID> {
    override fun <S : T?> findOne(example: Example<S>): Mono<S> {
        Assert.notNull(example, "Example must not be null!")
        val query: Query = this.exampleMapper.getMappedExample(example)
        return entityOperations.selectOne(query, example.probeType)
    }

    override fun <S : T?> findAll(example: Example<S>): Flux<S> {
        Assert.notNull(example, "Example must not be null!")
        return findAll(example, Sort.unsorted())
    }

    override fun <S : T?> findAll(example: Example<S>, sort: Sort): Flux<S> {
        Assert.notNull(example, "Example must not be null!")
        Assert.notNull(sort, "Sort must not be null!")
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        val query: Query = this.exampleMapper.getMappedExample(example).sort(sort)
        return entityOperations.select(query, example.probeType)
    }

    override fun <S : T?> count(example: Example<S>): Mono<Long> {
        Assert.notNull(example, "Example must not be null!")
        val query: Query = this.exampleMapper.getMappedExample(example)
        return entityOperations.count(query, example.probeType)
    }

    override fun <S : T?> exists(example: Example<S>): Mono<Boolean> {
        Assert.notNull(example, "Example must not be null!")
        val query: Query = this.exampleMapper.getMappedExample(example)
        return entityOperations.exists(query, example.probeType)
    }

    override fun <S : T, R : Any?, P : Publisher<R>> findBy(
        example: Example<S>,
        queryFunction: Function<FluentQuery.ReactiveFluentQuery<S>, P>
    ): P {

        Assert.notNull(example, "Sample must not be null!")
        Assert.notNull(queryFunction, "Query function must not be null!")

        return queryFunction.apply(ReactiveFluentQueryByExample<S, S>(example, example.probeType, this))
    }

}

