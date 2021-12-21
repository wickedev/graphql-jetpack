plugins {
    kotlin("jvm") version "1.6.10"
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api("com.graphql-java:graphql-java:17.3")
    api("org.springframework:spring-web")

    /* testing */
    testImplementation("io.kotest:kotest-runner-junit5:5.0.2")
    testImplementation("io.kotest:kotest-assertions-core:5.0.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}