package io.github.wickedev.graphql.repository;

import graphql.schema.DataFetchingEnvironment;
import io.github.wickedev.graphql.interfases.Node;
import io.github.wickedev.graphql.types.ID;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * In Kotlin findById, due to the return type of T or T?,
 * the user cannot freely write the desired type due to strict classification,
 * so it is written in Java.
 */
@NoRepositoryBean
public interface GraphQLDataLoaderByIdRepository<T extends Node> extends GraphQLDataLoaderRepository<T> {
    CompletableFuture<T> findById(ID id, DataFetchingEnvironment env);

    CompletableFuture<List<T>> findAllById(Iterable<ID> ids, DataFetchingEnvironment env);

    CompletableFuture<Boolean> existsById(ID id, DataFetchingEnvironment env);
}
