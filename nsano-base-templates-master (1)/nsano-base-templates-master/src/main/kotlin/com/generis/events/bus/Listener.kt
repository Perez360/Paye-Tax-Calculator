package com.generis.events.bus

import com.generis.model.APIResponse
import com.generis.util.JacksonUtils
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.slf4j.LoggerFactory

class Listener(override val di: DI) : CoroutineVerticle(), DIAware {
    private val log = LoggerFactory.getLogger(this::class.java)


    override suspend fun start() {
        super.start()

    }

    private fun reply(response: APIResponse<List<Any>>, message: Message<JsonObject>) {
        log.debug("${message.address()} - response from handler: $response")
        val convertedResponse = JsonObject(JacksonUtils.getJacksonMapper().writeValueAsString(response))
        message.reply(convertedResponse)
    }
}