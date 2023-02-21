package com.generis.stream.kafka.consumer


import com.generis.config.Communication
import com.generis.com.generis.stream.rabbitMq.StreamAddress
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.slf4j.LoggerFactory


class StreamConsumerService(override val di: DI) : CoroutineVerticle(), DIAware   {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val kafkaConsumer = Communication.getKafkaConsumer()

    override suspend fun start() {
        super.start()

        kafkaConsumer.handler { record ->

            logger.info("StreamAddress record.key() : " + record.key())
            logger.info("StreamAddress  record.offset(): " + record.offset())
            logger.info("StreamAddress record.partition() : " + record.partition())
            logger.info("StreamAddress record.value() : \n" + record.value())

        }

        val topics: MutableSet<String> = StreamAddress.values()
            .map { it.addressValue }
            .toMutableSet()

        kafkaConsumer.subscribe(topics).onSuccess {
            logger.info("StreamAddress message from success : $it")
        }.onFailure {
            logger.info("StreamAddress message from failure : $it")
        }

    }

}