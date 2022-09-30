plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.spring") version "1.6.10"
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("ru.vyarus.pom") version "2.2.1"
}

dependencies {
    api(project(":graphql-jetpack"))
    api(project(":graphql-kotlin-spring-security"))
    api(project(":graphql-kotlin-spring-webflux-upload"))
    optional("com.graphql-java:graphql-java:17.3")
    optional("com.graphql-java:graphql-java-extended-scalars:17.0")
    optional("com.expediagroup:graphql-kotlin-hooks-provider:5.3.1")
    optional("org.springframework.boot:spring-boot-autoconfigure")
    optional("io.github.wickedev:spring-security-jwt-webflux:0.3.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}