package com.generis.stream.kafka.producer

import com.generis.com.generis.stream.kafka.StreamTopic

interface StreamProducer {
    fun publish(jsonMessage: String, streamTopic: StreamTopic)
}