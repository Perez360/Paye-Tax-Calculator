package com.generis.config.plugins
import com.generis.com.generis.web.payeRoute
import com.generis.domain.UserRequestContext
import com.generis.logger
import com.generis.web.assetRoutes
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {

    intercept(ApplicationCallPipeline.Setup) {
        val headers = call.request.headers

        val language = headers["language"]
        val clientVersion = headers["clientVersion"] ?: ""
        val os = headers["operatingSystem"] ?: ""
        val modelNumber = headers["modelNumber"] ?: ""

        UserRequestContext.setCurrentLanguage(language)
        UserRequestContext.setCurrentClientVersion(clientVersion)
        UserRequestContext.setCurrentOS(os)
        UserRequestContext.setCurrentModelNumber(modelNumber)

        logger.info("current context is  :::  $UserRequestContext")

        return@intercept
    }

    routing {

        get("/ping") {
            application.log.info(">>> logging PING ALIVE")
            call.respondText("PONG!")
        }

        assetRoutes()
        payeRoute()
    }


}


