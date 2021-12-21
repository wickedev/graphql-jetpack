plugins {
    kotlin("jvm") version "1.6.10"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.6.0-RC2")
    api("io.projectreactor:reactor-core:3.4.13")
}