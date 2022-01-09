plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.spring") version "1.6.10"
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    api(project(":kotlin-coroutine-reactive-extensions"))
    api("com.jayway.jsonpath:json-path:2.6.0")
    api("org.springframework:spring-webflux")
    api("com.expediagroup:graphql-kotlin-spring-server:5.3.1")

    /* testing */
    testImplementation(project(":graphql-jetpack-starter"))
    testImplementation("io.kotest:kotest-runner-junit5:5.0.3")
    testImplementation("io.kotest:kotest-assertions-core:5.0.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.0")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }

}

tasks.withType<Test> {
    useJUnitPlatform()
}