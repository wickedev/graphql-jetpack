plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.spring") version "1.6.10"
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":graphql-jetpack-autoconfigure"))
    api("com.graphql-java:graphql-java:17.3")
    api("com.graphql-java:graphql-java-extended-scalars:17.0")
    api("com.expediagroup:graphql-kotlin-hooks-provider:5.3.1")
}
