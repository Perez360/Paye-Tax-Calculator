package com.generis.config
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream
import java.util.*
import kotlin.system.exitProcess

object Configuration {
    private val log = LoggerFactory.getLogger(this::class.java)
    private const val internalFile = "application.properties"
    private const val externalFile = "configurations/application.properties"
    var systemPropertiesFilePath:String?=null
    var commandLineFile: String = ""
    private lateinit var systemProperties:Properties

    fun loadSystemProperties(){
        val props = Properties()
        var stream: InputStream? = null
        val internalConfig = this::class.java.classLoader.getResource(internalFile)
        val externalConfig = File(externalFile)
        val commandLineConfig = File(commandLineFile)

        log.info("loading system properties")
        if(internalConfig!=null){
            log.info("Internal configuration file found")
            stream = internalConfig.openStream()
        }
        if(externalConfig.exists()){
            log.info("External configuration file found; overriding previous config file")
            stream = externalConfig.inputStream()
        }
        if(commandLineConfig.exists()){
            log.info("Configuration file passed through command line found; overriding previous config file")
            stream=commandLineConfig.inputStream()
        }
        if(stream==null){
            log.warn("System properties could not be loaded. " +
                    "Kindly verify the service was compiled with it's property file in the resource folder " +
                    "or passed as parameters in the command line params to run the application or set in $externalFile.")
            exitProcess(1)
        }
        props.load(stream)
        systemProperties = props
    }

    fun getSystemProperties():Properties{
        return systemProperties
    }
}