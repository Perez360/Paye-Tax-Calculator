package com.generis.integrations

import com.generis.exceptions.ServiceException

import com.fasterxml.jackson.module.kotlin.readValue
import com.generis.model.integration.FusionChargeResponseDto
import com.generis.model.integration.FusionDebitChargeRequestDto
import com.generis.model.integration.FusionDebitResponseDto
import com.generis.model.integration.FusionDebitWalletRequestDto
import com.generis.util.JacksonUtils
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.*


class FusionService(override val di: DI) : DIAware {

    private val systemConfig: Properties by instance(tag = "systemProperties")
    private val log = LoggerFactory.getLogger(this::class.java)

    fun debitWallet(fusionDebitWalletRequest: FusionDebitWalletRequestDto): FusionDebitResponseDto {

        val url = systemConfig.getProperty("fusion.api.url") +
                systemConfig.getProperty("fusion.api.path") +
                systemConfig.getProperty("fusion.api.key")

        val httpClient = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("kuwaita",systemConfig.getProperty("fusion.wallet.kuwaita"))
            .add("amount", fusionDebitWalletRequest.amount.toString())
            .add("mno", fusionDebitWalletRequest.mno.name)
            .add("refID", fusionDebitWalletRequest.transactionId)
            .add("msisdn", fusionDebitWalletRequest.senderMsisdn)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        try {
            val response:Response = httpClient.newCall(request).execute()

            log.info("response received ::: => $response  => ${response.body}")

            return JacksonUtils.getJacksonMapper().readValue(response.body!!.string())

        }catch (io: IOException){
            throw ServiceException(-5, io.localizedMessage)
        }catch (ex: Exception){
            throw ServiceException(-5, ex.localizedMessage)
        }
    }
    fun getDebitCharges(fusionDebitChargeRequest: FusionDebitChargeRequestDto): FusionChargeResponseDto {

        val url = systemConfig.getProperty("fusion.api.url") +
                systemConfig.getProperty("fusion.api.path") + "billing/"+
                systemConfig.getProperty("fusion.api.key")

        val httpClient = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("kuwaita",systemConfig.getProperty("fusion.wallet.kuwaita"))
            .add("amount", fusionDebitChargeRequest.amount.toString())
            .add("mno", fusionDebitChargeRequest.mno.name)
            .add("refID", fusionDebitChargeRequest.transactionId)
            .add("msisdn", fusionDebitChargeRequest.senderMsisdn)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        try {
            val response:Response = httpClient.newCall(request).execute()

            log.info("response received ::: => $response  => ${response.body}")

            return JacksonUtils.getJacksonMapper().readValue(response.body!!.string())

        }catch (io: IOException){
            throw ServiceException(-5, io.localizedMessage)
        }catch (ex: Exception){
            throw ServiceException(-5, ex.localizedMessage)
        }
    }
}