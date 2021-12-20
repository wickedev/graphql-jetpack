# GraphQL Jetpack

## Spring Data GraphQL

---

Spring Data GraphQL make easy to use Spring Data with `graphql-java` implementations ([`Kotlin GraphQL`](https://opensource.expediagroup.com/graphql-kotlin/docs/), [`DGS`](https://netflix.github.io/dgs/)). If you use `spring-data-r2dbc` or `spring-data-jdbc` directly, you have to write a lot of custom DataLoader. Spring Data GraphQL solves the N+1 problem through an internally auto-generated dataloader.




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

Relay Connection Spec 에 따르는 Connection 객체를 반환합니다.

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

아래 레포지토리 인터페이스들은 필요한 모든 인터페이스를 상속하여 편리하게 모든 쿼리 메서드에 접근 할 수 있습니다.

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

### **WARNING! 아래 기능은 아직 지원하지 않습니다.**
### [Query creation]((https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/#repositories.query-methods.query-creation)) (Plan to Support)

---

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

### **WARNING! 아래 기능은 아직 지원하지 않습니다.**
### Dyanmic Query Method (Plan to Support)

---

DataFetchingEnvironment 조사하여 컨벤션에 따라 동적으로 input에 일치하는 쿼리를 질의하여 반환합니다. [TypeGraphQL Prisma](https://prisma.typegraphql.com/)에서 영감을 받았습니다.

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

## **WARNING! 아래 기능은 아직 지원하지 않습니다.**
## Kotlin GraphQL Upload (Plan to Support)

---

Kotlin GraphQL의 [GraphQL Multipart Request Spec](https://github.com/jaydenseric/graphql-multipart-request-spec) 구현체 입니다.

```kotlin
class Upload(filePart: FilePart) : FilePart by filePart

@Compoment
class SampleQuery : Query {
    fun upload(files: List<Upload>): String = "${files.map { it.filename() }} Upload Successfully"

    fun upload(file: Upload): String = "${file.filename()} Upload Successfully"
}
```