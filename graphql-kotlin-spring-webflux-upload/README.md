# GraphQL Kotlin Upload

[GraphQL Multipart Request Spec](https://github.com/jaydenseric/graphql-multipart-request-spec) implementation of [GraphQL Kotlin](https://opensource.expediagroup.com/graphql-kotlin/docs/).

# Getting Started

## Gradle

```kotlin
repositories {
    maven("https://github.com/wickedev/graphql-jetpack/raw/deploy/maven-repo")
}

dependencies {
    implementation("io.github.wickedev:graphql-jetpack-starter:0.3.3")
}
```

## Example

```kotlin
class Upload(filePart: FilePart) : FilePart by filePart

@Compoment
class SampleMutation : Mutation {
    fun uploads(files: List<Upload>): String = "${files.map { it.filename() }} Upload Successfully"

    fun upload(file: Upload): String = "${file.filename()} Upload Successfully"
}
```