package com.generis.util

import org.modelmapper.ModelMapper
import org.modelmapper.config.Configuration
import org.modelmapper.convention.MatchingStrategies

class MapperDto() : ModelMapper() {
    init {
        configuration.matchingStrategy = MatchingStrategies.STRICT
        configuration.fieldAccessLevel = Configuration.AccessLevel.PRIVATE
        configuration.isFieldMatchingEnabled = true
        configuration.isSkipNullEnabled = true
    }
}

object Mapper {
    val mapper = MapperDto()

    inline fun <S, reified T> convert(source: S): T = mapper.map(source, T::class.java)
}