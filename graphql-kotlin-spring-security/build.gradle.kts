plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.spring") version "1.6.10"
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

dependencies {
    api(project(":graphql-core"))
    api(project(":kotlin-coroutine-reactive-extensions"))

    api("com.graphql-java:graphql-java:17.3")
    api("com.expediagroup:graphql-kotlin-spring-server:5.3.1")
    api("org.springframework.security:spring-security-core")
    api("com.zhokhov.graphql:graphql-java-datetime:4.1.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation(kotlin("stdlib-jdk8"))

    /* testing */
    testImplementation(project(":graphql-jetpack-autoconfigure"))
    testImplementation("io.github.wickedev:spring-security-jwt-webflux-starter:0.3.0")

    testImplementation("io.kotest:kotest-runner-junit5:5.0.3")
    testImplementation("io.kotest:kotest-assertions-core:5.0.3")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.0")

    testImplementation("com.expediagroup:graphql-kotlin-spring-server:5.3.1")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.boot:spring-boot-starter-security")

    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.bouncycastle:bcpkix-jdk15on:1.69")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }

}

tasks.withType<Test> {
    useJUnitPlatform()
}