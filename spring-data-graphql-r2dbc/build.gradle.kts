plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.spring") version "1.6.10"
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

dependencyManagement {
    imports {
        mavenBom("io.r2dbc:r2dbc-bom:Borca-M2")
        mavenBom("org.testcontainers:testcontainers-bom:1.16.2")
        mavenBom("io.netty:netty-bom:4.1.72.Final")
    }
}

dependencies {
    api(project(":graphql-core"))
    api(project(":spring-data-graphql-commons"))
    api(project(":kotlin-coroutine-reactive-extensions"))

    /* kotlin */
    api(kotlin("reflect"))
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    /* kotlin coroutine */
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
    api("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.5")

    /* spring data r2dbc */
    api("org.springframework.data:spring-data-r2dbc")

    /* graphql */
    api("com.graphql-java:graphql-java:17.3")
    api("com.graphql-java:java-dataloader:3.1.1")

    /* etc */
    api("net.bytebuddy:byte-buddy")

    /* testing */
    testImplementation("io.kotest:kotest-runner-junit5:5.0.2")
    testImplementation("io.kotest:kotest-assertions-core:5.0.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
