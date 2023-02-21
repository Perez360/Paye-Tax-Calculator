package com.generis.com.generis.repo

import com.generis.com.generis.model.Tax
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table


object Taxes : Table(name = "taxes") {
    val id: Column<Int> = integer("id").autoIncrement().uniqueIndex()
    val quoteId: Column<Int> =
        integer("quoteId").references(
            Quotes.id,
            ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val taxableAmount: Column<Double> = double("taxableAmount").default(0.00)
    val taxRate: Column<Double> = double("taxRate").default(0.00)
    val taxPaid: Column<Double> = double("taxPaid").default(0.00)

    override val primaryKey = PrimaryKey(id, name = "PK_Tax_ID")


    fun toTax(it: ResultRow): Tax =
        Tax(
            id = it[id],
            quoteId = it[quoteId],
            taxableAmount = it[taxableAmount],
            taxRate = it[taxRate],
            taxPaid = it[taxPaid]
        )


}