package io.github.wickedev.graphql.interfaces

import java.time.temporal.TemporalAccessor

interface SoftDeletable<T : TemporalAccessor> {
    val deletedAt: T?
}
