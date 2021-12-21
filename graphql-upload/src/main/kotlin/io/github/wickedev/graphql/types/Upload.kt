package io.github.wickedev.graphql.types

import org.springframework.http.codec.multipart.FilePart

class Upload(filePart: FilePart) : FilePart by filePart