package io.github.wickedev.graphql.`interface`

import java.time.temporal.TemporalAccessor

interface SoftDeletable<T : TemporalAccessor> {
    val deletedAt: T?
}
