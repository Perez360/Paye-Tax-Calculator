package com.generis.config

import com.generis.com.generis.repo.Quotes
import com.generis.com.generis.repo.Taxes
import com.generis.config.Configuration.getSystemProperties
import com.generis.repo.Assets
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun connect() {
        log.info("Initialising database")
        val pool = hikari()
        Database.connect(pool)
        runUpdateAndMigration()
        //runFlyway(pool)
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig().apply {
            this.jdbcUrl = getSystemProperties().getProperty("hikari.jdbcUrl")
            this.driverClassName = getSystemProperties().getProperty("hikari.driverClassName")
            this.username = getSystemProperties().getProperty("hikari.dataSource.user")
            this.password = getSystemProperties().getProperty("hikari.dataSource.password")
            this.isAutoCommit = true
            this.maximumPoolSize = getSystemProperties().getProperty("hikari.dataSource.maxPoolSize", "10").toInt()
        }

        // logging the database configurations
        log.info("jdbc url ::: ${getSystemProperties().getProperty("hikari.jdbcUrl")}")
        log.info("database user from the configuration::: ${getSystemProperties().getProperty("hikari.dataSource.user")}")
        log.info("password  ::: ${getSystemProperties().getProperty("hikari.dataSource.password")}")
        config.validate()
        return HikariDataSource(config)
    }

    private fun runUpdateAndMigration() {
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.createMissingTablesAndColumns(Assets)
            SchemaUtils.createMissingTablesAndColumns(Quotes)
            SchemaUtils.createMissingTablesAndColumns(Taxes)
        }
    }

    private fun runFlyway(datasource: HikariDataSource) {
        val flyway = Flyway.configure()
            .dataSource(datasource)
            .load()
        try {
            flyway.info()
            //we may use flyway.migrate()
        } catch (e: Exception) {
            log.error("Exception running flyway migration", e)
            throw e
        }
        log.info("Flyway migration has finished")
    }

}
