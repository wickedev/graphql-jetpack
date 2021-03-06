@file:Suppress("DEPRECATION")

package io.github.wickedev.graphql.spring.data.r2dbc.factory

import io.github.wickedev.graphql.spring.data.r2dbc.strategy.AdditionalIsNewStrategy
import io.github.wickedev.graphql.spring.data.r2dbc.strategy.GraphQLAdditionalIsNewStrategy
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.getBeanProvider
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.data.mapping.callback.ReactiveEntityCallbacks
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactoryBean
import org.springframework.data.repository.Repository
import org.springframework.data.repository.core.support.RepositoryFactorySupport
import org.springframework.r2dbc.core.DatabaseClient

class GraphQLR2dbcRepositoryFactoryBean<T : Repository<S, ID>, S, ID : java.io.Serializable>(
    repositoryInterface: Class<out T?>,
) : R2dbcRepositoryFactoryBean<T, S, ID>(repositoryInterface), ApplicationContextAware {

    private lateinit var additionalIsNewStrategy: AdditionalIsNewStrategy

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.additionalIsNewStrategy =
            beanFactory.getBeanProvider<AdditionalIsNewStrategy>().getIfAvailable { GraphQLAdditionalIsNewStrategy() }
        super.setBeanFactory(beanFactory)
    }

    override fun getFactoryInstance(
        client: DatabaseClient,
        dataAccessStrategy: ReactiveDataAccessStrategy
    ): RepositoryFactorySupport {
        return GraphQLSimpleR2dbcRepositoryFactory(client, dataAccessStrategy, additionalIsNewStrategy)
    }

    override fun getFactoryInstance(operations: R2dbcEntityOperations): RepositoryFactorySupport {
        return GraphQLSimpleR2dbcRepositoryFactory(operations, additionalIsNewStrategy)
    }
}
