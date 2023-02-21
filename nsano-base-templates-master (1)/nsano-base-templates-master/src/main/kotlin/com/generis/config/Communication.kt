package com.generis.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.generis.config.Configuration.systemPropertiesFilePath
import com.generis.controller.AssetImpl
import com.generis.repo.AssetService
import com.generis.com.generis.stream.kafka.StreamTopic
import com.generis.config.Configuration.getSystemProperties
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.*
import io.vertx.core.json.JsonObject
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.kafka.client.consumer.KafkaConsumer
import io.vertx.kafka.client.producer.KafkaProducer
import io.vertx.kafka.client.producer.KafkaProducerRecord
import io.vertx.rabbitmq.RabbitMQClient
import io.vertx.rabbitmq.RabbitMQOptions
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager
import org.kodein.di.DI
import org.kodein.di.bindConstant
import org.kodein.di.bindSingleton
import org.slf4j.LoggerFactory


object Communication {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private lateinit var vertx:Vertx
    private lateinit var rabbitMQClient: RabbitMQClient
    private lateinit var kafkaConsumer: KafkaConsumer<String, String>
    private lateinit var kafkaProducer: KafkaProducer<String, String>
    private var di : DI

    init {
         di = DI {
            bindConstant(tag = "systemProperties") { getSystemProperties() }
            bindSingleton { vertx }

            bindSingleton { AssetImpl(di) }
            bindSingleton { AssetService() }
        }
    }

    fun initializeEventBus():Future<String>{
        val vertxSetupPromise = Promise.promise<String>()
        val deploymentPromise = Promise.promise<String>()
        configureMapper()
        if(getSystemProperties().getProperty("vertx.eventBus.startCluster").toBooleanStrict()){
            val zkConfig = JsonObject()
            zkConfig.put("zookeeperHosts", getSystemProperties().getProperty("zk.zookeeperHosts"))
            zkConfig.put("rootPath", getSystemProperties().getProperty("zk.rootPath"))
            zkConfig.put(
                "retry", JsonObject()
                    .put("initialSleepTime", getSystemProperties().getProperty("zk.retry.initialSleepTime").toLong())
                    .put("maxTimes", getSystemProperties().getProperty("zk.retry.maxTimes").toLong())
            )
            val mgr: ClusterManager = ZookeeperClusterManager(zkConfig)
            val options = VertxOptions().setClusterManager(mgr)
            Vertx.clusteredVertx(options).onSuccess {
                vertx = it
                deployEventBuses(deploymentPromise)
                vertxSetupPromise.complete("Vertx in cluster mode ready for use")
            }.onFailure {
                logger.error("Cannot start vertx in cluster mode.",it)
                deploymentPromise.fail("Cannot deploy anything")
                vertxSetupPromise.fail("Cannot start vertx in cluster mode. ${it.message}")
            }
        }else{
            //start normal here
            vertx = Vertx.vertx()
            deployEventBuses(deploymentPromise)
            vertxSetupPromise.complete("Vertx in standalone mode ready for use")
        }

        val resultFuture = Promise.promise<String>()
        CompositeFuture.all(vertxSetupPromise.future(),deploymentPromise.future()).onSuccess {
            resultFuture.complete("Everything is set! let's deploy")
        }.onFailure {
            logger.warn("Something went wrong",it)
            resultFuture.fail("Everything is not set! ${it.message}")
        }
        return resultFuture.future()
    }

    fun initializeRabbitMQ(){

        if (!getSystemProperties().getProperty("vertx.rabbitMq.startCluster").toBooleanStrict())
            return

        val config = RabbitMQOptions()
        config.virtualHost = "fusionDemo"
        config.isAutomaticRecoveryEnabled = true

        rabbitMQClient = RabbitMQClient.create(vertx, config)
        rabbitMQClient.start { asyncResult: AsyncResult<Void?> ->
            if (asyncResult.succeeded()) {
                logger.info("RabbitMQ successfully connected!")

                val consumers = StreamTopic.values().map { it }.toList()
                val meta = JsonObject()
                meta.put("x-message-ttl", 10_000L);

                logger.info("Registering detected message queues!")

                for (consumer in consumers){
                    rabbitMQClient.queueDeclare(consumer.topicValue, true, false, false)
                    logger.info("Registered Consumer for :: " + consumer.topicValue)
                }

                deployRabbitMqConsumers()
            } else {
                logger.warn("Fail to connect to RabbitMQ " + asyncResult.cause().message)
            }
        }
    }

    fun initializeKafka(){

        if (!getSystemProperties().getProperty("vertx.Kafka.startCluster").toBooleanStrict())
            return

        val config: MutableMap<String, String> = HashMap()
        config["bootstrap.servers"] = getSystemProperties().getProperty("vertx.Kafka.host")
        config["key.serializer"] = getSystemProperties().getProperty("kafka.key.serializer")
        config["value.serializer"] = getSystemProperties().getProperty("kafka.key.serializer")
        config["key.deserializer"] = getSystemProperties().getProperty("kafka.key.deserializer")
        config["value.deserializer"] = getSystemProperties().getProperty("kafka.value.deserializer")

        kafkaProducer = KafkaProducer.create(vertx, config)

        config["group.id"] = getSystemProperties().getProperty("vertx.Kafka.groupId")
        config["auto.offset.reset"] = getSystemProperties().getProperty("kafka.auto.offset.reset")
        config["enable.auto.commit"] = getSystemProperties().getProperty("kafka.enable.auto.commit")
        config["acks"] = getSystemProperties().getProperty("kafka.acks.value")

        kafkaConsumer = KafkaConsumer.create(vertx, config)

        deployKafkaConsumers()
    }

    private fun deployEventBuses(promise: Promise<String>) {
        val deploymentOptions = DeploymentOptions()
            .setWorker(getSystemProperties().getProperty("vertx.rabbitMq.setWorker").toBooleanStrict())


        val deployListener = vertx.deployVerticle(com.generis.events.bus.Listener(di), deploymentOptions)

        val result = CompositeFuture.all(listOf(deployListener))
        result.onSuccess{
            logger.info("Verticle(s) have been deployed over the event bus")
            promise.complete("All modules have been deployed on the event bus")
        }.onFailure{
            logger.warn("Verticle(s) for event bus have failed to be deployed",it)
            promise.fail("Some modules failed to be deployed. ${it.message}")
        }
    }

    private fun deployRabbitMqConsumers() {

        val deploymentOptions = DeploymentOptions()
            .setWorker(getSystemProperties().getProperty("vertx.rabbitMq.setWorker").toBooleanStrict())

        val deployConsumer = vertx.deployVerticle(com.generis.stream.rabbitMq.consumer.StreamConsumerService(di), deploymentOptions)

        val result  = CompositeFuture.all(listOf(deployConsumer))
        result.onSuccess{
            logger.info("Verticle(s) have been deployed over the rabbit client")
        }.onFailure{
            logger.warn("Verticle(s) for rabbit client have failed to be deployed",it)
        }
    }

    private fun deployKafkaConsumers() {

        val deploymentOptions = DeploymentOptions()
            .setWorker(getSystemProperties().getProperty("vertx.Kafka.setWorker").toBooleanStrict())

        val deployConsumer = vertx.deployVerticle(com.generis.stream.kafka.consumer.StreamConsumerService(di), deploymentOptions)

        val result  = CompositeFuture.all(listOf(deployConsumer))
        result.onSuccess{
            logger.info("Verticle(s) have been deployed over kafka consumers")
        }.onFailure{
            logger.warn("Verticle(s) for kafka consumers have failed to be deployed",it)
        }
    }


    private fun file():ConfigStoreOptions {
        return ConfigStoreOptions()
            .setType("file")
            .setFormat("properties")
            .setConfig(JsonObject().put("path", systemPropertiesFilePath))
    }
    private fun configureMapper(){
        DatabindCodec.mapper().registerModule(ParameterNamesModule())
            .registerModule(Jdk8Module())
            .registerModule(JavaTimeModule())
            .registerKotlinModule()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        DatabindCodec.prettyMapper().registerModule(ParameterNamesModule())
            .registerModule(Jdk8Module())
            .registerModule(JavaTimeModule())
            .registerKotlinModule()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    fun getVertx():Vertx{
        return vertx
    }

    fun getRabbitMQClient() : RabbitMQClient{
        return rabbitMQClient;
    }

    fun getKafkaConsumer() : KafkaConsumer<String, String>{
        return kafkaConsumer;
    }


    fun getKafkaProducer() : KafkaProducer<String, String>{
        return kafkaProducer;
    }
}