package io.github.wickedev.graphql.repository

import io.github.wickedev.graphql.interfases.Node
import io.github.wickedev.graphql.types.ID
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository

@NoRepositoryBean
interface GraphQLDataLoaderRepository<T: Node?>: Repository<T, ID>