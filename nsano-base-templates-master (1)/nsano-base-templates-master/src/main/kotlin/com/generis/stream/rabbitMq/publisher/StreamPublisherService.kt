package com.generis.stream.rabbitMq.publisher


import com.generis.com.generis.stream.rabbitMq.StreamAddress
import com.generis.config.Communication
import io.vertx.core.AsyncResult
import io.vertx.core.buffer.Buffer
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets

class StreamPublisherService(override val di: DI) : StreamPublisher, DIAware  {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val rabbitMQClient = Communication.getRabbitMQClient()

    override fun publish(jsonMessage: String, address: StreamAddress) {
        val message = Buffer.buffer(jsonMessage, StandardCharsets.UTF_8.name())



        rabbitMQClient.basicPublish("", address.addressValue, message) { pubResult: AsyncResult<Void?> ->
            if (pubResult.succeeded()) {
                logger.warn("SUCCESS: publish message pushed is :::$jsonMessage")
            } else {
                pubResult.cause().printStackTrace()
                logger.warn("ERROR: publish :: " + pubResult.cause().message)
            }
        }
    }

    override  fun publishWithConfirm(jsonMessage: String, address: StreamAddress): Boolean {
        var response = false
        val message = Buffer.buffer(jsonMessage, StandardCharsets.UTF_8.name())

        rabbitMQClient.confirmSelect { confirmResult ->
            if (confirmResult.succeeded()) {
                rabbitMQClient.basicPublish("", address.addressValue, message) { pubResult ->
                    if (pubResult.succeeded()) {
                        // Check the message got confirmed by the broker.
                        rabbitMQClient.waitForConfirms { waitResult ->
                            if (waitResult.succeeded()){
                                logger.warn("SUCCESS: publishWithConfirm message pushed is :::$jsonMessage")
                                response = true
                            } else {
                                waitResult.cause().printStackTrace()
                            }
                        }
                    } else {
                        pubResult.cause().printStackTrace()
                        logger.warn("ERROR: publishWithConfirm :: " + pubResult.cause().message)
                    }
                }
            } else {
                confirmResult.cause().printStackTrace()
                logger.warn("ERROR: publishWithConfirm :: " + confirmResult.cause().message)
            }
        }
        return response
    }
}