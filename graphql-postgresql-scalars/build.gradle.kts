plugins {
    kotlin("jvm") version "1.6.10"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    api(project(":graphql-core"))
    api("io.r2dbc:r2dbc-postgresql:0.8.10.RELEASE")
    api("com.graphql-java:graphql-java:17.3")
    api("com.graphql-java:graphql-java-extended-scalars:17.0")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
}