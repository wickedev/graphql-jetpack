plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.spring") version "1.6.10"
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

dependencyManagement {
    imports {
        mavenBom("io.r2dbc:r2dbc-bom:Arabba-SR11")
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
    testImplementation(project(":spring-data-graphql-r2dbc-autoconfigure"))
    testImplementation("io.kotest:kotest-runner-junit5:5.0.2")
    testImplementation("io.kotest:kotest-assertions-core:5.0.2")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.0")
    testImplementation(project(":kotlin-coroutine-reactive-extensions"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }

    /* testing testcontainers */
    testImplementation("org.testcontainers:postgresql:1.16.2")
    testImplementation("org.testcontainers:r2dbc:1.16.2")
    testImplementation("io.r2dbc:r2dbc-postgresql:0.8.10.RELEASE")
    runtimeOnly("org.postgresql:postgresql:42.3.1")

    /* testing fixture */
    testImplementation("com.appmattus.fixture:fixture:1.2.0")
    testImplementation("com.appmattus.fixture:fixture-generex:1.2.0")
    testImplementation("com.appmattus.fixture:fixture-javafaker:1.2.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
