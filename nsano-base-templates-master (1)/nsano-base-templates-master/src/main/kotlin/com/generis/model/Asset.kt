package com.generis.model

import com.generis.domain.MODEL_VERSION
import com.fasterxml.jackson.annotation.JsonFormat
import com.generis.util.JacksonUtils
import com.generis.util.LocalDateSerializer
import com.generis.util.LocalDateTimeSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import uk.co.jemos.podam.common.PodamLongValue
import java.time.LocalDate
import java.time.LocalDateTime


@Serializable
data class Asset(
    @Required
    var id: Int? = null,
    @Required
    var name: String = "",
    @Required
    var customerId: String = "",
    @Required
    var customerMsisdn: String = "",
    @Required
    var extras: String = "",

    @Required
    var value: Double = 0.00,

    @Required
    var currency: String = "GHC",
    @Required
    val size: Int = 0,

    @Required
    var registrationNumber: String = "",

    @JsonFormat(pattern = JacksonUtils.datePattern)
    @Serializable(with = LocalDateSerializer::class)
    var registrationDate: LocalDate = LocalDate.now(),

    @JsonFormat(pattern = JacksonUtils.dateTimePattern)
    @Serializable(with = LocalDateTimeSerializer::class)
    var createdDate: LocalDateTime = LocalDateTime.now(),
    @JsonFormat(pattern = JacksonUtils.dateTimePattern)
    @Serializable(with = LocalDateTimeSerializer::class)
    var updatedDate: LocalDateTime = LocalDateTime.now(),
    @PodamLongValue(
        minValue = MODEL_VERSION,
        maxValue = MODEL_VERSION
    ) //We want our version to always be the same as our current table version
    var version: Long = MODEL_VERSION
)

data class SearchAndFilterAsset(
    val name: String?,
    var customerId: String?,
    var customerMsisdn: String?,
    var valueGreaterThanEq: Double?,
    var valueLessThanEq: Double?,
    var currency: String?,
    var registrationNumber: String?,
    var registrationDate: String?,
    var page: Int?,
    var size: Int?
) {
    companion object {
        fun from(map: Map<String, Any?>) = object {
            val name: String by map
            val customerId: String by map
            val customerMsisdn: String? by map
            val valueGreaterThanEq: Double? by map
            val valueLessThanEq: Double? by map
            val currency: String? by map
            val registrationNumber: String? by map
            val registrationDate: String? by map
            val page: Int? by map
            val size: Int? by map

            val data = SearchAndFilterAsset(
                name, customerId, customerMsisdn, valueGreaterThanEq, valueLessThanEq, currency, registrationNumber, registrationDate, page, size
            )
        }.data
    }
}

@Serializable
data class CreateAssetDto(
    var name: String = "",
    @Required
    var customerId: String = "",
    @Required
    var customerMsisdn: String = "",
    var extras: String = "",

    var value: Double = 0.00,
    var currency: String = "GHS",
    val size: Int = 0,

    @Required
    var registrationNumber: String = "",

    @JsonFormat(pattern = JacksonUtils.datePattern)
    @Serializable(with = LocalDateSerializer::class)
    var registrationDate: LocalDate = LocalDate.now(),
)

@Serializable
data class UpdateAssetDto(
    @Required
    var id: Int,
    var name: String = "",
    @Required
    var customerId: String = "",
    @Required
    var customerMsisdn: String = "",
    var extras: String = "",

    var value: Double = 0.00,
    val size: Int = 0,

    @Required
    var currency: String = "GHC",

    @Required
    var registrationNumber: String = "",

    @JsonFormat(pattern = JacksonUtils.datePattern)
    @Serializable(with = LocalDateSerializer::class)
    var registrationDate: LocalDate = LocalDate.now(),
)