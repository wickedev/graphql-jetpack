package io.github.wickedev.graphql.spring.data.r2dbc

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension

@Suppress("unused")
object ProjectConfig : AbstractProjectConfig() {
    override fun extensions() = listOf(SpringExtension)
}