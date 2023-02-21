package com.generis.model

import kotlinx.serialization.Serializable


@Serializable
data class APIResponse<T>(
    val systemCode: String,
    val code: String,
    val message: String,
    val data: T?
)


@Serializable
data class ErrorResponse(
    val systemCode: String,
    val code: String,
    val message: String
)


data class FusionCallback(
    val authorRefID:String,
    val msg: String,
    val metadataID:String,
    val code:String,
    val transactionID:String,
    val systemCode:String,
    val systemMsg:String,
    /*@JsonFormat(pattern = dateTimePattern)
    val date:LocalDateTime,*///cannot configure java time module with Ktor so i've skipped for now
    val balBefore:String,
    val type:String,
    val userID:String,
    val network:String,
    val refID:String

)



