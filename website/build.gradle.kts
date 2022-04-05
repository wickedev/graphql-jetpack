import com.moowork.gradle.node.npm.NpmTask

plugins {
    id("base")
    id("distribution")
    id("com.moowork.node") version "1.3.1"
}

task<NpmTask>("npmRunBuild") {
    group = "npm"
    setArgs(listOf("run", "build"))
}

task<NpmTask>("npmStart") {
    group = "npm"
    setArgs(listOf("run", "start"))
}

tasks.getByName("npmRunBuild").dependsOn("npmInstall")
tasks.getByName("npmStart").dependsOn("npmInstall")

task<Copy>("deploySite") {
    group = "npm"
    dependsOn("npmRunBuild")
    from(project.buildDir)
    into("${rootProject.projectDir}/docs")
}

tasks.getByName<Delete>("clean") {
    group = "npm"
    delete = setOf("node_modules", "build", ".docusaurus")
}