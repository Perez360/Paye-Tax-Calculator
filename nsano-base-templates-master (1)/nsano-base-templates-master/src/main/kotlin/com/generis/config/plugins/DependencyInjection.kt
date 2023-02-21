package com.generis.config.plugins

import com.generis.com.generis.controller.QuoteControllerImpl
import com.generis.com.generis.repo.QuoteService
import com.generis.com.generis.repo.TaxService
import com.generis.config.Communication
import com.generis.config.Configuration.getSystemProperties
import com.generis.controller.AssetImpl
import com.generis.events.ServiceEventsListener
import com.generis.integrations.MailService
import com.generis.integrations.SmsService
import com.generis.repo.AssetService
import com.generis.stream.kafka.producer.StreamProducerService
import com.generis.stream.rabbitMq.publisher.StreamPublisherService
import io.ktor.application.*
import org.kodein.di.bindConstant
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.ktor.di


fun Application.configureDI() {
    di {
        //System and Framework bindings
        bindConstant(tag = "systemProperties") { getSystemProperties() }
        bindSingleton { Communication.getVertx() }

        bindProvider { ServiceEventsListener(di) }
        bindProvider { StreamPublisherService(di) }
        bindProvider { StreamProducerService(di) }

        bindSingleton { SmsService(di) }
        bindSingleton { MailService(di) }

        // bindings for a resources
        bindSingleton { AssetImpl(di) }
        bindSingleton { AssetService() }

        bindSingleton { QuoteControllerImpl(di) }
        bindSingleton { QuoteService(di) }

        bindSingleton { TaxService() }
    }

}

