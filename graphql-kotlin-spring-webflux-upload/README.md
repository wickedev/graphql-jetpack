# Kotlin GraphQL Upload

[GraphQL Multipart Request Spec](https://github.com/jaydenseric/graphql-multipart-request-spec) implementation of [GraphQL Kotlin](https://opensource.expediagroup.com/graphql-kotlin/docs/).

```kotlin
class Upload(filePart: FilePart) : FilePart by filePart

@Compoment
class SampleQuery : Query {
    fun upload(files: List<Upload>): String = "${files.map { it.filename() }} Upload Successfully"

    fun upload(file: Upload): String = "${file.filename()} Upload Successfully"
}
```