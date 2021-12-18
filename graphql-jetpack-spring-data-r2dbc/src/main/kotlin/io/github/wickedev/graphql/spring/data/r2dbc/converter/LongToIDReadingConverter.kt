package io.github.wickedev.graphql.spring.data.r2dbc.converter

import io.github.wickedev.graphql.scalars.ID
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class LongToIDReadingConverter : Converter<Long, ID> {
    override fun convert(source: Long): ID {
        return ID("", source.toString())
    }
}