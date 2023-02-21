package com.generis.config

import com.newrelic.telemetry.Attributes
import com.newrelic.telemetry.micrometer.NewRelicRegistry
import com.newrelic.telemetry.micrometer.NewRelicRegistryConfig
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.core.instrument.util.NamedThreadFactory
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.micrometer.MicrometerMetricsOptions
import org.slf4j.LoggerFactory
import java.net.InetAddress
import java.time.Duration

object Monitoring {

    private val log = LoggerFactory.getLogger(this::class.java)
    private lateinit var vertx:Vertx


    fun configureMonitoring(){
        // new relic registry options
        fun newRelicConfig(): NewRelicRegistryConfig {
            return object : NewRelicRegistryConfig {
                override fun get(key: String): String? {
                    return null
                }

                override fun apiKey(): String {
                    return Configuration.getSystemProperties().getProperty("newRelic.api.key")
                }
                override fun step(): Duration {
                    return Duration.ofSeconds(4)
                }

                override fun serviceName(): String {
                    return "backend-generis"
                }
            }
        }

        log.info("new relic configuration value found:: ${newRelicConfig().step()}")

        val newRelicRegistry = NewRelicRegistry.builder(newRelicConfig())
            .commonAttributes(
                Attributes()
                    .put("host", InetAddress.getLocalHost().hostName)
            )
            .build()
        newRelicRegistry.config().meterFilter(MeterFilter.ignoreTags("plz_ignore_me"))
        newRelicRegistry.config().meterFilter(MeterFilter.denyNameStartsWith("jvm.threads"))
        newRelicRegistry.config().meterFilter(MeterFilter.acceptNameStartsWith(""))
        newRelicRegistry.start(NamedThreadFactory("newrelic.micrometer.registry"))

        vertx = Vertx.vertx(
            VertxOptions().setMetricsOptions(
            MicrometerMetricsOptions()
                .setMicrometerRegistry(newRelicRegistry)
                .setEnabled(true)
        ))

    }
}