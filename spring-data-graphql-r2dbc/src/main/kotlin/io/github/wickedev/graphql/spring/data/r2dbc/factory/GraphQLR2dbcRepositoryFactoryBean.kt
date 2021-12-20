@file:Suppress("DEPRECATION")

package io.github.wickedev.graphql.spring.data.r2dbc.factory

import io.github.wickedev.graphql.spring.data.r2dbc.strategy.AdditionalIsNewStrategy
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.getBean
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactoryBean
import org.springframework.data.repository.Repository
import org.springframework.data.repository.core.support.RepositoryFactorySupport
import org.springframework.r2dbc.core.DatabaseClient

class GraphQLR2dbcRepositoryFactoryBean<T : Repository<S, ID>, S, ID : java.io.Serializable>(
    repositoryInterface: Class<out T?>,
) : R2dbcRepositoryFactoryBean<T, S, ID>(repositoryInterface) {

    private lateinit var additionalIsNewStrategy: AdditionalIsNewStrategy

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.additionalIsNewStrategy = beanFactory.getBean()
        super.setBeanFactory(beanFactory)
    }

    override fun getFactoryInstance(
        client: DatabaseClient,
        dataAccessStrategy: ReactiveDataAccessStrategy
    ): RepositoryFactorySupport {
        return GraphQLSimpleR2dbcRepositoryFactory(client, dataAccessStrategy,  additionalIsNewStrategy)
    }

    override fun getFactoryInstance(operations: R2dbcEntityOperations): RepositoryFactorySupport {
        return GraphQLSimpleR2dbcRepositoryFactory(operations,  additionalIsNewStrategy)
    }
}
