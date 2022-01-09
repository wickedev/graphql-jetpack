plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.spring") version "1.6.10"
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("ru.vyarus.pom") version "2.2.1"
}

repositories {
    mavenCentral()
}


dependencies {
    api(project(":spring-data-graphql-r2dbc"))
    optional("org.springframework.data:spring-data-r2dbc")
    optional("org.springframework.boot:spring-boot-autoconfigure")
}

tasks.withType<Test> {
    useJUnitPlatform()
}