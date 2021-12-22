plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.spring") version "1.6.10"
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

dependencies {
    implementation(project(":graphql-core"))
    implementation(project(":spring-data-graphql-commons"))
    implementation(project(":kotlin-coroutine-reactive-extensions"))

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    api("com.graphql-java:graphql-java:17.3")
    api("com.expediagroup:graphql-kotlin-spring-server:5.3.1")
    api("org.springframework.security:spring-security-core:5.6.0")

    /* testing */
    testImplementation("io.kotest:kotest-runner-junit5:5.0.2")
    testImplementation("io.kotest:kotest-assertions-core:5.0.2")

    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.0")
    testImplementation("com.expediagroup:graphql-kotlin-spring-server:5.3.1")
    testImplementation("com.expediagroup:graphql-kotlin-federation:5.3.1")


    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.boot:spring-boot-starter-security")
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