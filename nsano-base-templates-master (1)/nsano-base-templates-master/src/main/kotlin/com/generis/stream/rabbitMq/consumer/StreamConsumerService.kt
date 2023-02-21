package com.generis.stream.rabbitMq.consumer


import com.fasterxml.jackson.module.kotlin.readValue
import com.generis.com.generis.stream.kafka.StreamTopic
import com.generis.config.Communication
import com.generis.controller.AssetController
import com.generis.model.CreateAssetDto
import com.generis.model.UpdateAssetDto
import com.generis.util.JacksonUtils
import io.vertx.core.AsyncResult

import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.rabbitmq.RabbitMQConsumer
import io.vertx.rabbitmq.RabbitMQMessage
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.slf4j.LoggerFactory

class StreamConsumerService(override val di: DI) : CoroutineVerticle(), DIAware   {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val assetController: AssetController by di.instance()

    private val rabbitMQClient = Communication.getRabbitMQClient()

    override suspend fun start() {
        super.start()

        rabbitMQClient.basicConsumer(StreamTopic.CREATE_ASSET.topicValue) { rabbitMQConsumerAsyncResult: AsyncResult<RabbitMQConsumer> ->
            if (rabbitMQConsumerAsyncResult.succeeded()) {
                val mqConsumer = rabbitMQConsumerAsyncResult.result()
                mqConsumer.handler { message: RabbitMQMessage ->
                    logger.info("createAsset : \n" + message.body().toString())

                    try {
                        val createAsset: CreateAssetDto = JacksonUtils.getJacksonMapper().readValue(message.body().toString())
                        assetController.create(createAsset)
                    }catch (e:Exception){
                        e.printStackTrace()
                        logger.warn(e.localizedMessage)
                    }
                }
            } else {
                rabbitMQConsumerAsyncResult.cause().printStackTrace()
            }
        }

        rabbitMQClient.basicConsumer(StreamTopic.UPDATE_ASSET.topicValue) { rabbitMQConsumerAsyncResult: AsyncResult<RabbitMQConsumer> ->
            if (rabbitMQConsumerAsyncResult.succeeded()) {
                val mqConsumer = rabbitMQConsumerAsyncResult.result()
                mqConsumer.handler { message: RabbitMQMessage ->
                    logger.info("updateAsset : \n" + message.body().toString())

                    val updateAssetDTO: UpdateAssetDto = JacksonUtils.getJacksonMapper().readValue(message.body().toString())
                    assetController.update(updateAssetDTO)
                }
            } else {
                rabbitMQConsumerAsyncResult.cause().printStackTrace()
            }
        }
    }

}