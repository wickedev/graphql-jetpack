![GraphQL Jetpack](assets/graphql-jetpack.svg)

A collection of packages for easily writing Kotlin GraphQL server implementations. Current major targets are [GraphQL Kotlin](https://opensource.expediagroup.com/graphql-kotlin/docs/), [Spring Webflux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html), and [Spring Data R2DBC](https://spring.io/projects/spring-data-r2dbc).

# Getting Started

## Gradle

```kotlin
repositories {
    maven("https://github.com/wickedev/graphql-jetpack/raw/deploy/maven-repo")
}

dependencies {
    implementation("io.github.wickedev:graphql-jetpack-starter:0.4.6")
    implementation("io.github.wickedev:spring-data-graphql-r2dbc-starter:0.4.6")
}
```

## [Spring Data GraphQL](spring-data-graphql-r2dbc)

Spring Data GraphQL make easy to use [Spring Data](https://spring.io/projects/spring-data) with [graphql-java](https://github.com/graphql-java/graphql-java) implementations like [GraphQL Kotlin](https://opensource.expediagroup.com/graphql-kotlin/docs/), [DGS](https://netflix.github.io/dgs/). If you use [Spring Data R2DBC](https://spring.io/projects/spring-data-r2dbc) or [Spring Data JDBC](https://spring.io/projects/spring-data-jdbc) directly, you have to write a lot of custom DataLoader. Spring Data GraphQL solves the N+1 problem through an internally auto-generated DataLoader.

In addition, it satisfies relay specifications such as Global Object Identification, Node, and Connection.

_※ Currently, only spring-data-r2dbc is supported._

## [GraphQL Kotlin Spring Security](graphql-kotlin-spring-security)

Spring Security integration library with Custom GraphQL Directive `@auth`

## [GraphQL Kotlin Upload](graphql-kotlin-spring-webflux-upload)

[GraphQL Multipart Request Spec](https://github.com/jaydenseric/graphql-multipart-request-spec) implementation of [GraphQL Kotlin](https://opensource.expediagroup.com/graphql-kotlin/docs/).

# License

```
The MIT License

Copyright (c) 2021 Ryan Yang, http://codesanctum.net

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```