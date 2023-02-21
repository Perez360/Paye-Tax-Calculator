package com.generis.repo

import com.generis.domain.MODEL_VERSION
import com.generis.model.Asset
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDate
import java.time.LocalDateTime


object Assets : Table(name = "assets") {
    val id: Column<Int> = integer("id").autoIncrement().uniqueIndex()
    val name: Column<String> = varchar("name", 60).index()
    val customerId: Column<String> = varchar("customerId", 20).index()
    val customerMsisdn: Column<String> = varchar("customerMsisdn", 15).index()

    val value: Column<Double> = double("value").default(0.00)
    val currency: Column<String> = varchar("currency", 5)

    val registrationNumber: Column<String> = varchar("registrationNumber", 25)
    val registrationDate: Column<LocalDate> = date("registrationDate" ).index()
    val size: Column<Int> = integer("size").default(0)

    val extras: Column<String> = varchar("extras", 256).default("{}")

    val createdDate: Column<LocalDateTime> = datetime("createdDate").default(LocalDateTime.now()).index()
    val updatedDate: Column<LocalDateTime> = datetime("updatedDate").default(LocalDateTime.now()).index()
    val version: Column<Long> = long("version").default(MODEL_VERSION)

    override val primaryKey = PrimaryKey(id, name = "PK_assets_ID")

    fun toAssets(row: ResultRow): Asset =
        Asset(
            id = row[id],
            name = row[name],
            customerId = row[customerId],
            customerMsisdn = row[customerMsisdn],
            extras = row[extras],
            value = row[value],
            currency = row[currency],
            registrationNumber = row[registrationNumber],
            registrationDate = row[registrationDate],

            size = row[size],

            createdDate = row[createdDate],
            updatedDate = row[updatedDate],
            version = row[version]
        )
}
