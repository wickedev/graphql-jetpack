plugins {
    kotlin("jvm") version "1.6.10"
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

dependencies {
    implementation(project(":graphql-core"))
    implementation(kotlin("stdlib-jdk8"))
    api("org.springframework.data:spring-data-commons")
    api("com.graphql-java:graphql-java:17.3")
}