package com.generis

import com.generis.config.Configuration.commandLineFile
import com.generis.config.Configuration.loadSystemProperties
import com.generis.config.plugins.configureHTTP
import com.generis.config.plugins.configureSerialization
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*

@Suppress("unused")
fun Application.moduleTest(/*aDI: DI*/){
    commandLineFile = environment.config.propertyOrNull("service.config")?.getString() ?:""
    loadSystemProperties() //load the system properties
    configureSerialization()
    configureHTTP()
    //routing
    routing {
        get("/ping") {
            application.log.info(">>> logging PING ALIVE")
            call.respondText("PONG!")
        }
    }
}