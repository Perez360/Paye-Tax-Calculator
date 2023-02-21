package com.generis.integrations

import com.generis.exceptions.ServiceException
import com.generis.model.integration.MailRequestDto
import com.generis.model.integration.MailResponseDto
import com.fasterxml.jackson.module.kotlin.readValue
import com.generis.util.JacksonUtils
import okhttp3.*
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.*


class MailService(override val di: DI) : DIAware {

    private val systemConfig: Properties by instance(tag = "systemProperties")
    private val logger = LoggerFactory.getLogger(this::class.java)

     fun sendMail(mailRequestDto: MailRequestDto): MailResponseDto {

        val url = systemConfig.getProperty("email.smtp.host")

        val httpClient = OkHttpClient()

         val formBody = FormBody.Builder()
             .add("subject", mailRequestDto.subject)
             .add("message", mailRequestDto.message)
             .add("recipients", mailRequestDto.recipients)
             .add("signature", mailRequestDto.signature)
             .add("sender_name", mailRequestDto.senderName)
             .build()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .post(formBody)
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

    fun sendSupportMail(msg: String, subject: String): MailResponseDto {

        val url = systemConfig.getProperty("email.smtp.host")
        val receipts = systemConfig.getProperty("support.email.address")
        val signature = systemConfig.getProperty("email.signature.id")
        val senderId = systemConfig.getProperty("email.sender.id")

        val httpClient = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("subject", subject)
            .add("message", msg)
            .add("recipients", receipts)
            .add("signature", signature)
            .add("sender_name", senderId)
            .build()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .post(formBody)
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