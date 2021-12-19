plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.spring") version "1.6.10"
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    /* kotlin coroutine */
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.5")

    /* spring */
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("io.r2dbc:r2dbc-postgresql:0.8.10.RELEASE")

    /* graphql */
    implementation("com.graphql-java:graphql-java:17.3")
    implementation("com.graphql-java:java-dataloader:3.1.1")

    /* testing */
    testImplementation("io.kotest:kotest-runner-junit5:5.0.2")
    testImplementation("io.kotest:kotest-assertions-core:5.0.2")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }

    /* testing testcontainers */
    testImplementation("org.testcontainers:postgresql:1.16.2")
    testImplementation("org.testcontainers:r2dbc:1.16.2")
    runtimeOnly("org.postgresql:postgresql:42.3.1")

    /* testing fixture */
    testImplementation("com.appmattus.fixture:fixture:1.2.0")
    testImplementation("com.appmattus.fixture:fixture-generex:1.2.0")
    testImplementation("com.appmattus.fixture:fixture-javafaker:1.2.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
