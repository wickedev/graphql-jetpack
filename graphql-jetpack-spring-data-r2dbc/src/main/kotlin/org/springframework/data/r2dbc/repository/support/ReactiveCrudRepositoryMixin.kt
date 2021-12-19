package org.springframework.data.r2dbc.repository.support

import io.github.wickedev.graphql.spring.data.r2dbc.repository.base.PropertyRepository
import org.reactivestreams.Publisher
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.data.util.Streamable
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.Assert
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@NoRepositoryBean
interface ReactiveCrudRepositoryMixin<T, ID> : ReactiveCrudRepository<T, ID>, PropertyRepository<T, ID> {
    /* (non-Javadoc)
     * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#save(S)
     */
    @Transactional
    override fun <S : T> save(objectToSave: S): Mono<S> {
        Assert.notNull(objectToSave, "Object to save must not be null!")
        return if (entity.isNew(objectToSave)) {
            entityOperations.insert(objectToSave)
        } else entityOperations.update(objectToSave)
    }

    /* (non-Javadoc)
	 * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#saveAll(java.lang.Iterable)
	 */
    @Transactional
    override fun <S : T> saveAll(objectsToSave: Iterable<S>): Flux<S> {
        Assert.notNull(objectsToSave, "Objects to save must not be null!")
        return Flux.fromIterable(objectsToSave).concatMap { objectToSave: S ->
            save(
                objectToSave
            )
        }
    }

    /* (non-Javadoc)
	 * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#saveAll(org.reactivestreams.Publisher)
	 */
    @Transactional
    override fun <S : T> saveAll(objectsToSave: Publisher<S>): Flux<S> {
        Assert.notNull(objectsToSave, "Object publisher must not be null!")
        return Flux.from(objectsToSave).concatMap { objectToSave: S ->
            save(
                objectToSave
            )
        }
    }

    /* (non-Javadoc)
	 * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#findById(java.lang.Object)
	 */
    override fun findById(id: ID): Mono<T> {
        Assert.notNull(id, "Id must not be null!")
        return entityOperations.selectOne(getIdQuery(id), entity.javaType)
    }

    /* (non-Javadoc)
	 * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#findById(org.reactivestreams.Publisher)
	 */
    override fun findById(publisher: Publisher<ID>): Mono<T> {
        return Mono.from(publisher).flatMap { id: ID -> this.findById(id) }
    }

    /* (non-Javadoc)
	 * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#existsById(java.lang.Object)
	 */
    override fun existsById(id: ID): Mono<Boolean> {
        Assert.notNull(id, "Id must not be null!")
        return entityOperations.exists(getIdQuery(id), entity.javaType)
    }

    /* (non-Javadoc)
	 * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#existsById(org.reactivestreams.Publisher)
	 */
    override fun existsById(publisher: Publisher<ID>): Mono<Boolean> {
        return Mono.from(publisher).flatMap { id: ID -> this.findById(id) }.hasElement()
    }

    /* (non-Javadoc)
	 * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#findAll()
	 */
    override fun findAll(): Flux<T> {
        return entityOperations.select(Query.empty(), entity.javaType)
    }

    /* (non-Javadoc)
	 * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#findAllById(java.lang.Iterable)
	 */
    override fun findAllById(iterable: Iterable<ID>): Flux<T> {
        Assert.notNull(iterable, "The iterable of Id's must not be null!")
        return findAllById(Flux.fromIterable(iterable))
    }

    /* (non-Javadoc)
	 * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#findAllById(org.reactivestreams.Publisher)
	 */
    override fun findAllById(idPublisher: Publisher<ID>): Flux<T> {
        Assert.notNull(idPublisher, "The Id Publisher must not be null!")
        return Flux.from(idPublisher).buffer().filter { ids: List<ID> -> ids.isNotEmpty() }
            .concatMap { ids: List<ID> ->
                if (ids.isEmpty()) {
                    return@concatMap Flux.empty()
                }
                val idProperty = getIdProperty().name

                entityOperations.select(
                    Query.query(
                        Criteria.where(
                            idProperty
                        ).`in`(ids)
                    ),
                    entity.javaType
                )
            }
    }

    /* (non-Javadoc)
	 * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#count()
	 */
    override fun count(): Mono<Long> {
        return entityOperations.count(Query.empty(), entity.javaType)
    }

    /* (non-Javadoc)
	 * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#deleteById(java.lang.Object)
	 */
    @Transactional
    override fun deleteById(id: ID): Mono<Void> {
        Assert.notNull(id, "Id must not be null!")
        return entityOperations.delete(getIdQuery(id), entity.javaType).then()
    }

    /* (non-Javadoc)
	 * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#deleteById(org.reactivestreams.Publisher)
	 */
    @Transactional
    override fun deleteById(idPublisher: Publisher<ID>): Mono<Void> {
        Assert.notNull(idPublisher, "The Id Publisher must not be null!")
        return Flux.from(idPublisher).buffer().filter { ids: List<ID> -> ids.isNotEmpty() }
            .concatMap { ids: List<ID?> ->
                if (ids.isEmpty()) {
                    return@concatMap Flux.empty<Int>()
                }
                val idProperty = getIdProperty().name
                entityOperations.delete(
                    Query.query(
                        Criteria.where(
                            idProperty
                        ).`in`(ids)
                    ),
                    entity.javaType
                )
            }.then()
    }

    /* (non-Javadoc)
	 * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#delete(java.lang.Object)
	 */
    @Transactional
    override fun delete(objectToDelete: T): Mono<Void> {
        Assert.notNull(objectToDelete, "Object to delete must not be null!")
        return deleteById(entity.getRequiredId(objectToDelete))
    }

    /* (non-Javadoc)
	 * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#deleteAll(java.lang.Iterable)
	 */
    @Transactional
    override fun deleteAll(iterable: Iterable<T>): Mono<Void> {
        Assert.notNull(iterable, "The iterable of Id's must not be null!")
        return deleteAll(Flux.fromIterable(iterable))
    }

    /* (non-Javadoc)
	 * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#deleteAll(org.reactivestreams.Publisher)
	 */
    @Transactional
    override fun deleteAll(objectPublisher: Publisher<out T>): Mono<Void> {
        Assert.notNull(objectPublisher, "The Object Publisher must not be null!")
        val idPublisher = Flux.from(objectPublisher) //
            .map { entity: T -> this.entity.getRequiredId(entity) }
        return deleteById(idPublisher)
    }

    /*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#deleteAll()
	 */
    @Transactional
    override fun deleteAll(): Mono<Void?>? {
        return entityOperations.delete(Query.empty(), entity.javaType).then()
    }

    /*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.reactive.ReactiveCrudRepository#deleteAllById(java.lang.Iterable)
	 */
    override fun deleteAllById(ids: Iterable<ID>): Mono<Void> {
        Assert.notNull(ids, "The iterable of Id's must not be null!")
        val idsList = Streamable.of(ids).toList()
        val idProperty = getIdProperty().name
        return entityOperations.delete(Query.query(Criteria.where(idProperty).`in`(idsList)), entity.javaType)
            .then()
    }
}