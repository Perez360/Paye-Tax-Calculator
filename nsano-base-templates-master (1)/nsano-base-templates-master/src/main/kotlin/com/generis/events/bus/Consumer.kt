package com.generis.events.bus

import com.generis.events.ServiceEventsListener
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.slf4j.LoggerFactory

/**
 * Consumes all events / actions made available over the event bus for the trade card service
 * */
class Consumer(override val di: DI) : CoroutineVerticle(), DIAware {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val serviceEventsListener: ServiceEventsListener by di.instance()

    override suspend fun start() {
        super.start()
    }

}