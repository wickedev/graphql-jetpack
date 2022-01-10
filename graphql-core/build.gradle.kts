plugins {
    kotlin("jvm") version "1.6.10"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api(kotlin("reflect"))
    api("com.graphql-java:graphql-java:17.3")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")

    /* testing */
    testImplementation("io.kotest:kotest-runner-junit5:5.0.3")
    testImplementation("io.kotest:kotest-assertions-core:5.0.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}