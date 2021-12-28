plugins {
    kotlin("jvm") version "1.6.10"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api("com.graphql-java:graphql-java:17.3")

    /* testing */
    testImplementation("io.kotest:kotest-runner-junit5:5.0.2")
    testImplementation("io.kotest:kotest-assertions-core:5.0.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}