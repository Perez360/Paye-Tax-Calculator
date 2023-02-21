package com.generis.config.plugins

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import org.slf4j.event.Level

fun Application.configureHTTP() {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost()
    }

    install(CallLogging){
        level = Level.DEBUG
    }

}
