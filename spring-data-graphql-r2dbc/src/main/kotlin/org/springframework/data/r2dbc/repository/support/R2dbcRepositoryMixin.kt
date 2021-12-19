package org.springframework.data.r2dbc.repository.support

import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface R2dbcRepositoryMixin<T, ID> :
    ReactiveSortingRepositoryMixin<T, ID>,
    ReactiveQueryByExampleExecutorMixin<T, ID>