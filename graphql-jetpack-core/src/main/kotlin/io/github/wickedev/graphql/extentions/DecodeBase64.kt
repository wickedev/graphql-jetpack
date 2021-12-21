package io.github.wickedev.graphql.extentions

import io.github.wickedev.graphql.types.ID
import io.github.wickedev.graphql.utils.decoder
import java.nio.charset.StandardCharsets

fun String.toLocalID(): ID {
    return try {
        val decoded = String(decoder.decode(this), StandardCharsets.UTF_8)
        val split = decoded.split(":".toRegex(), 2).toTypedArray()
        require(split.size == 2) { String.format("expecting a valid global id, got %s", this) }
        val type = split.getOrElse(0) { "" }
        val value = split.getOrElse(1) { "" }

        ID(type, value)
    } catch (e: IllegalArgumentException) {
        ID(this)
    }
}