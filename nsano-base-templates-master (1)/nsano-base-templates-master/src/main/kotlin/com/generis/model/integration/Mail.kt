package com.generis.model.integration

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable


@Serializable
data class MailRequestDto(
    @Required
    val quoteId: Int,
    @Required
    val subject: String,
    @Required
    var message: String,
    @Required
    val recipients: String,
    @Required
    val signature: String,
    @Required
    val senderName: String,
    @Required
    val senderEmail: String? = "Banbo",
    @Required
    val password: String? = "",
) {
    fun toRequestJsonString(): String {
        return "{" +
                "subject: '$subject', " +
                "message: '$message'," +
                "recipients: '$recipients'," +
                "signature: '$signature'," +
                "sender_name:'$senderName', " +
                "sender_email: '$senderEmail'," +
                "password:'$password" +
                "}"
    }
}

@Serializable
data class MailResponseDto(
    val msg: String,
    val code: String
)

