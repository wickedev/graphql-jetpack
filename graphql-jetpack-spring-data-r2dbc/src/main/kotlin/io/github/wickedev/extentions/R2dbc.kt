package io.github.wickedev.extentions

import org.springframework.data.domain.Sort

val Sort.Direction.inverted: Sort.Direction
    get() = if (isAscending) Sort.Direction.DESC else Sort.Direction.ASC