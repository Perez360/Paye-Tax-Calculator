package com.generis.events

import com.generis.config.Communication
import com.generis.util.JacksonUtils.getJacksonMapper
import io.vertx.core.Vertx
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.JsonObject
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instanceOrNull
import org.slf4j.LoggerFactory

/**
 * Broadcast events over the eventBus
 * */
class ServiceEventsListener(override val di: DI) : EventListener, DIAware {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val vertx: Vertx? by di.instanceOrNull()

    /**
     * Use the event bus and broadcast on the right address based on the right eventType
     * */
    override fun notify(eventType: EventType, data: Any, headers: Map<String, String>?) {

        val toSend = JsonObject(getJacksonMapper().writeValueAsString(data))
        val deliveryOptions = DeliveryOptions()
        headers?.let {
            it.forEach { (k, v) ->
                deliveryOptions.addHeader(k, v)
            }
        }
        log.debug("publishing $data on ${eventType.broadcastAddress}")
        if (vertx == null) {
            log.warn("VERT.X did not load by injection !!! Switching to use dependency from core library")
            Communication.getVertx().eventBus().publish(eventType.broadcastAddress, toSend, deliveryOptions)
        } else {
            log.trace("using vertx through DI to emit event")
            vertx?.eventBus()?.publish(eventType.broadcastAddress, toSend, deliveryOptions)
        }
    }
}