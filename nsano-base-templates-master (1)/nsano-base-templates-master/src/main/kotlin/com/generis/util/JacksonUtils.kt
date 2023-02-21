package com.generis.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import java.math.BigDecimal
import java.math.RoundingMode

object JacksonUtils {
    /**
     * Returns an object mapper used to convert Jackson JSON object to kotlin classes and vice versa*/
    private val objectMapperJackson: ObjectMapper = ObjectMapper()
        .registerModule(ParameterNamesModule())
        .registerModule(Jdk8Module())
        .registerModule(JavaTimeModule())
        .registerKotlinModule()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
        .disable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    fun getJacksonMapper(): ObjectMapper {
        return objectMapperJackson
    }

    /**
     * Returns a double to a specific number of decimal places
     * */
    fun Double.toDecimalPlaces(dp:Int):Double{
        return BigDecimal.valueOf(this).setScale(dp, RoundingMode.CEILING).toDouble()
    }

    const val dateTimePattern = "yyyy-MM-dd'T'HH:mm:ss.SSS"

    const val datePattern = "yyyy-MM-dd"

}