package com.generis.stream.rabbitMq.publisher

import com.generis.com.generis.stream.rabbitMq.StreamAddress

interface StreamPublisher {
    fun publish(jsonMessage: String, address: StreamAddress)
    fun publishWithConfirm(jsonMessage: String, address: StreamAddress): Boolean
}