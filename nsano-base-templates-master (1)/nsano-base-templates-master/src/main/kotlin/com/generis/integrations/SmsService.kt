package com.generis.integrations

import com.generis.exceptions.ServiceException
import com.generis.model.integration.SMSRequestDto
import com.generis.model.integration.SMSResponseDto
import com.fasterxml.jackson.module.kotlin.readValue
import com.generis.util.JacksonUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.*


class SmsService(override val di: DI) : DIAware {

    private val systemConfig: Properties by instance(tag = "systemProperties")
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun sendSms(smsRequestDto: SMSRequestDto): SMSResponseDto {
        val url = systemConfig.getProperty("sms.api.url") +
                systemConfig.getProperty("sms.api.path")

        val httpClient = OkHttpClient()


//        val smsRequestDto = SMSRequestDto(
//            sender = systemConfig.getProperty("sms.sender.id"),
//            recipient = ,
//            message = msg
//        )


        val requestBody = smsRequestDto
            .toRequestJsonString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        println(requestBody.contentType()?.type)

        val request = Request.Builder()
            .url(url)
            .addHeader("X-SMS-Apikey", systemConfig.getProperty("sms.api.key"))
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        try {

            val response:Response = httpClient
                .newCall(request)
                .execute()

            logger.info("response received ::: => $response  => ${response.body}")

            if (response.code in 200..299)
                return JacksonUtils.getJacksonMapper().readValue(response.body!!.string())

            throw ServiceException(-5, response.message)

        }catch (io: IOException){
            throw ServiceException(-5, io.localizedMessage)
        }catch (ex: Exception){
            throw ServiceException(-5, ex.localizedMessage)
        }
    }


}