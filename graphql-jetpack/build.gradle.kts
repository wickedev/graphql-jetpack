plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.spring") version "1.6.10"
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    api(project(":graphql-kotlin-spring-security"))
    api("com.expediagroup:graphql-kotlin-spring-server:5.3.1")
    api("org.springframework.security:spring-security-core")
}

tasks.withType<Test> {
    useJUnitPlatform()
}