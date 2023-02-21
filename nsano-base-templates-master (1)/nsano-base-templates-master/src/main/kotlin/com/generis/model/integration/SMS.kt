package com.generis.model.integration

import kotlinx.serialization.Serializable


@Serializable
data class SMSRequestDto(
    var quoteId:Int,
    var sender: String = "",
    var recipient: String,
    var message: String
) {
    fun toRequestJsonString(): String {
        return "{\"sender\": \"$sender\",\"recipient\":\"$recipient\",\"message\":\"$message\"}"
    }
}

@Serializable
data class SMSId(
    val id: String
)

@Serializable
data class SMSResponseDto(
    val code: String,
    val msg: String,
    val data: SMSId? = null
)

