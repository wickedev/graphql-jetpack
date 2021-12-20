# GraphQL Jetpack

A collection of packages for easily writing Java GraphQL server implementations.

## Spring Data GraphQL

---

Spring Data GraphQL make easy to use [Spring Data](https://spring.io/projects/spring-data) with [graphql-java](https://github.com/graphql-java/graphql-java) implementations like [Kotlin GraphQL](https://opensource.expediagroup.com/graphql-kotlin/docs/), [DGS](https://netflix.github.io/dgs/). If you use [Spring Data R2DBC](https://spring.io/projects/spring-data-r2dbc) or [Spring Data JDBC](https://spring.io/projects/spring-data-jdbc) directly, you have to write a lot of custom DataLoader. Spring Data GraphQL solves the N+1 problem through an internally auto-generated dataloader.

_※ Currently, only spring-data-r2dbc is supported._

### Node & GraphQLNodeRepository

---

Spring Data GraphQL conforms to the [Relay GraphQL Server Specification](https://relay.dev/docs/guides/graphql-server-specification/). The `GraphQLNodeRepository.findNodeById` method delegates to the `GraphQLRepository` or `GraphQLDataLoaderRepository` implementations registered as Spring Beans. Entities not registered in `GraphQLRepository`, `GraphQLR2dbcRepository`, or `GraphQLCrudRepository` cannot be search.

```kotlin
interface Node {
    val id: ID
}

interface GraphQLNodeRepository : Repository<Node, ID> {

    fun findNodeById(id: ID, env: DataFetchingEnvironment): CompletableFuture<Node?>
}
```

### GraphQLDataLoaderRepository

---

`GraphQLDataLoaderRepository` uses an internally auto-generated data loader to perform optimized query. For example, if `findById` is called multiple times in different Spring Data implementations, `SELECT * FROM tables WHERE id = $id` query will be called multiple times. However, `GraphQLDataLoaderRepository` queries `SELECT * FROM tables WHERE id IN ($ids)` SQL only once with ids that have been deduplicated through `DataLoader`.


```kotlin
interface GraphQLDataLoaderRepository<T : Node> : Repository<T, ID> {

    fun findById(id: ID, env: DataFetchingEnvironment): CompletableFuture<T?>

    fun findAllById(ids: Iterable<ID>, env: DataFetchingEnvironment): CompletableFuture<List<T>>

    fun existsById(id: ID, env: DataFetchingEnvironment): CompletableFuture<Boolean>
}
```

### GraphQLDataLoaderConnectionsRepository

---

Returns a Connection conforming to the [Relay Connection Spec](https://relay.dev/graphql/connections.htm).

```kotlin
interface GraphQLDataLoaderConnectionsRepository<T : Node> : Repository<T, ID> {

    fun findAllBackwardConnectById(
        last: Int?,
        before: ID?,
        env: DataFetchingEnvironment
    ): CompletableFuture<Connection<T>>

    fun findAllForwardConnectById(
        first: Int?,
        after: ID?,
        env: DataFetchingEnvironment
    ): CompletableFuture<Connection<T>>
}
```

### GraphQLRepository, GraphQLR2dbcRepository, GraphQLCrudRepository

---

The repository interfaces below inherit all necessary interfaces, so you can conveniently access all query methods.

```kotlin
interface GraphQLRepository<T : Node> :
    GraphQLDataLoaderConnectionsRepository<T>,
    GraphQLDataLoaderRepository<T>
```

```kotlin
// for spring-data-r2dbc
interface GraphQLR2dbcRepository<T : Node> :
    GraphQLRepository<T>,
    R2dbcRepository<T, ID>

// for spring-data-jdbc (not yet support)
interface GraphQLCrudRepository<T : Node> :
    GraphQLRepository<T>,
    CrudRepository<T, ID>
```

### [Query creation]((https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/#repositories.query-methods.query-creation)) (Plan to Support)

---

**WARNING! THE FEATURES BELOW ARE NOT YET SUPPORTED.**

```kotlin
interface PersonRepository : GraphQLRepository<Person> {

    fun findByEmailAddressAndLastname(
        emailAddress: EmailAddress,
        lastname: String,
        env: DataFetchingEnvironment
    ): CompletableFuture<List<Person>>

    // Enables the distinct flag for the query
    fun findDistinctPeopleByLastnameOrFirstname(
        lastname: String,
        firstname: String,
        env: DataFetchingEnvironment
    ): CompletableFuture<List<Person>>
    fun findPeopleDistinctByLastnameOrFirstname(
        lastname: String,
        firstname: String,
        env: DataFetchingEnvironment
    ): CompletableFuture<List<Person>>

    // Enabling ignoring case for an individual property
    fun findByLastnameIgnoreCase(
        lastname: String,
        env: DataFetchingEnvironment
    ): CompletableFuture<List<Person>>

    // Enabling ignoring case for all suitable properties
    fun findByLastnameAndFirstnameAllIgnoreCase(
        lastname: String,
        firstname: String,
        env: DataFetchingEnvironment
    ): CompletableFuture<List<Person>>

    // Enabling static ORDER BY for a query
    fun findByLastnameOrderByFirstnameAsc(
        lastname: String,
        env: DataFetchingEnvironment
    ): CompletableFuture<List<Person>>
    fun findByLastnameOrderByFirstnameDesc(
        lastname: String,
        env: DataFetchingEnvironment
    ): CompletableFuture<List<Person>>
}
```

### Dyanmic Query Method (Plan to Support)

---

**WARNING! THE FEATURES BELOW ARE NOT YET SUPPORTED.**

Query and return according to the input convention. Inspired by [TypeGraphQL Prisma] (https://prisma.typegraphql.com/).

```graphql
type Query {
    user(where: UserWhereUniqueInput!): User

    users(
        where: UserWhereInput
        orderBy: [UserOrderByInput!]
        cursor: UserWhereUniqueInput
        take: Int
        skip: Int
        distinct: [UserScalarFieldEnum!]
    ): [User!]!
}
```

```kotlin
interface GraphQLDynamicQueryRepository<T : Node> : Repository<Node, ID> {

    fun findBy(env: DataFetchingEnvironment): CompletableFuture<T?>

    fun findAllBy(env: DataFetchingEnvironment): CompletableFuture<List<T>>
}
```


## Kotlin GraphQL Upload (Plan to Support)

---

**WARNING! THE FEATURES BELOW ARE NOT YET SUPPORTED.**

[GraphQL Multipart Request Spec](https://github.com/jaydenseric/graphql-multipart-request-spec) implementation of [Kotlin GraphQL](https://opensource.expediagroup.com/graphql-kotlin/docs/).

```kotlin
class Upload(filePart: FilePart) : FilePart by filePart

@Compoment
class SampleQuery : Query {
    fun upload(files: List<Upload>): String = "${files.map { it.filename() }} Upload Successfully"

    fun upload(file: Upload): String = "${file.filename()} Upload Successfully"
}
```