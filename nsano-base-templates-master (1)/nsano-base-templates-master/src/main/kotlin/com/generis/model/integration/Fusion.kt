package com.generis.model.integration

import com.generis.enums.FusionSupportedNetworkEnum
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class FusionAPIResponse<T>(
    val code: String,
    val msg: String,
    val data: T?
)

@Serializable
data class FusionDebitWalletRequestDto(
    val senderMsisdn: String,
    val amount: Double,
    val mno: FusionSupportedNetworkEnum,
    val authToken: String,
    var transactionId: String = ""
)

@Serializable
data class FusionDebitChargeRequestDto(
    val senderMsisdn: String,
    val amount: Double,
    val mno: FusionSupportedNetworkEnum,
    val authToken: String,
    var transactionId: String = ""
)

@Serializable
data class FusionDebitResponseDto(
    val msg: String?,
    val code:String?,
    val reference:String?,
)

@Serializable
data class FusionRequestResponse(
    val msg: String,
    val reference:String?,
    val code:String,
    val transactionID:String?
)


@Serializable
data class FusionWalletDebitRequestPayload(
    val kuwaita: String,
    val amount: Double,
    val mno: FusionSupportedNetworkEnum,
    val authToken: String? = null,
    val msisdn: String,
)

@Serializable
data class FusionDebitResponse(

    @Required
    val msg: String,
    val date: String,

    @Required
    val code: String,

    @Required
    @SerialName("system_msg")
    val systemMsg: String,

    @Required
    @SerialName("system_code")
    val systemCode: String,

    @Required
    val authorRefID: String,
    @Required
    val balBefore: String,
    @Required
    val type: String,

    @Required
    val userID: String,

    @Required
    @SerialName("transactionID")
    val transactionId: String,

    @Required
    val network: FusionSupportedNetworkEnum,

    @Required
    val reference: String,

    val balAfter: String,

    val metadataID: String,

    @Required
    @SerialName("author_ref")
    val authorRef: String,

    @Required
    @SerialName("refID")
    val refId: String
)

@Serializable
data class FusionChargeResponseDto(
    val code:String?,
    val msg: FusionCharge
)

@Serializable
data class FusionCharge(
    val deferred: Boolean,
    val charge: Double,
    val applyOnUser: Boolean,
    val operationCharge: Double,
    val taxComponent: FusionTaxComponent
)

@Serializable
data class FusionTaxComponent(
    val sumTaxesCharged: Double,
    val taxes: MutableList<FusionTax>
)

@Serializable
data class FusionTax(
    val taxableAmount: Double,
    val charge: Double,
    val name: String,
    val id: String? = null,
)