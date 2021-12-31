plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.spring") version "1.6.10"
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

dependencies {
    implementation(project(":graphql-core"))
    implementation(project(":spring-data-graphql-commons"))
    implementation(project(":kotlin-coroutine-reactive-extensions"))

    /* kotlin */
    implementation(kotlin("reflect"))
    // implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    /* kotlin coroutine */
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.5")

    /* spring data r2dbc */
    implementation("org.springframework.data:spring-data-r2dbc")

    /* graphql */
    implementation("com.graphql-java:graphql-java:17.3")
    implementation("com.graphql-java:java-dataloader:3.1.1")

    /* etc */
    implementation("net.bytebuddy:byte-buddy")

    /* testing */
    testImplementation("io.kotest:kotest-runner-junit5:5.0.2")
    testImplementation("io.kotest:kotest-assertions-core:5.0.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}