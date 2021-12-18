package io.github.wickedev.graphql.spring.data.r2dbc.converter

import io.github.wickedev.graphql.scalars.ID
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class IDToLongWritingConverter : Converter<ID, Long?> {
    override fun convert(source: ID): Long? {
        return if (source.value.isNotEmpty()) {
            source.value.toLong()
        } else {
            null
        }
    }
}