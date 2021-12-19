package io.github.wickedev.graphql.scalars

import java.nio.charset.StandardCharsets
import java.util.*

internal val encoder: Base64.Encoder = Base64.getUrlEncoder().withoutPadding()

internal fun String.encodeBase64(): String {
    return encoder.encodeToString(toByteArray(StandardCharsets.UTF_8))
}