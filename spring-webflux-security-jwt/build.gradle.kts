plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.spring") version "1.6.10"
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    api(project(":graphql-core"))
    api(project(":spring-data-graphql-commons"))
    api(project(":kotlin-coroutine-reactive-extensions"))

    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    api("org.springframework.security:spring-security-core")
    api("org.springframework.security:spring-security-web")
    api("org.springframework.boot:spring-boot-starter-webflux")

    api("io.jsonwebtoken:jjwt-api:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")

    /* testing */
    testImplementation("io.kotest:kotest-runner-junit5:5.0.2")
    testImplementation("io.kotest:kotest-assertions-core:5.0.2")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.0")

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