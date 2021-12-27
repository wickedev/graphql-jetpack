subprojects {
    val groupId: String by project
    val version: String by project

    apply {
        plugin("java-library")
        plugin("maven-publish")
    }

    repositories {
        mavenCentral()
    }

    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(project.the<SourceSetContainer>()["main"].allSource)
    }

    artifacts {
        add("archives", sourcesJar)
    }

    publishing {
        repositories {
            maven {
                url = uri("$rootDir/maven-repo")
            }
        }

        publications {
            publications.create<MavenPublication>("maven") {
                artifactId = project.name
                setGroupId(groupId)
                setVersion(version)
                from(components["java"])
                artifact(sourcesJar.get())
            }
        }
    }

}

fun Project.publishing(configure: Action<PublishingExtension>): Unit =
    (this as ExtensionAware).extensions.configure("publishing", configure)

fun String.kebabToCamelCase(): String {
    val snakeRegex = "-[a-zA-Z]".toRegex()
    return snakeRegex.replace(this) {
        return@replace it.value.replace("-", "")
            .toUpperCase(java.util.Locale.getDefault())
    }
}