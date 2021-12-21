package io.github.wickedev.graphql.spring.data.r2dbc.repository.mixin

import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface R2dbcRepositoryMixin<T, ID> :
    ReactiveSortingRepositoryMixin<T, ID>,
    ReactiveQueryByExampleExecutorMixin<T, ID>