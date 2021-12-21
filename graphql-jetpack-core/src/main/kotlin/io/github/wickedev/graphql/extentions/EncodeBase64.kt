package io.github.wickedev.graphql.extentions

import io.github.wickedev.graphql.utils.encoder
import java.nio.charset.StandardCharsets

internal fun String.encodeBase64(): String {
    return encoder.encodeToString(toByteArray(StandardCharsets.UTF_8))
}