package com.generis.stream.kafka.producer


import com.generis.com.generis.stream.kafka.StreamTopic
import com.generis.config.Communication
import com.generis.exceptions.ServiceException
import io.vertx.kafka.client.producer.KafkaProducerRecord
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.slf4j.LoggerFactory

class StreamProducerService(override val di: DI) : StreamProducer, DIAware  {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val kafkaProducer= Communication.getKafkaProducer()


    override fun publish(jsonMessage: String, streamTopic: StreamTopic) {

        val record = KafkaProducerRecord.create<String, String>(
            streamTopic.topicValue,
            jsonMessage
        )

        kafkaProducer.send(record)
            .onSuccess {

                logger.warn("SUCCESS: publishing message on ${it.topic} with message :::${jsonMessage}")

            }.onFailure {

            logger.warn("ERROR: publishing on ${streamTopic.topicValue}  with:: " + jsonMessage)
            logger.warn("ERROR: publishing on ${streamTopic.topicValue}  error:: " + it.localizedMessage)
                throw ServiceException(-5, "kafka publish error")
        }
    }

}