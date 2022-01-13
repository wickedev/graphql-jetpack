subprojects {
    val groupId: String by project
    val version: String by project

    apply {
        plugin("java-library")
        plugin("maven-publish")
    }

    repositories {
        mavenCentral()
        maven("https://github.com/wickedev/spring-security-jwt-webflux/raw/deploy/maven-repo")
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
