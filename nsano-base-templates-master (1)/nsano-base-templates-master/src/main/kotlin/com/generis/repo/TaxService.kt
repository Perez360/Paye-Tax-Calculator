package com.generis.com.generis.repo

import com.generis.com.generis.model.CreateTaxDto
import com.generis.com.generis.model.Tax
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class TaxService : TaxDAO {
    override fun create(listOfTaxes: List<CreateTaxDto>): ResultRow? = transaction {
        Taxes.batchInsert(listOfTaxes) {
            this[Taxes.quoteId] = it.quoteId!!
            this[Taxes.taxableAmount] = it.taxableAmount
            this[Taxes.taxRate] = it.taxRate
            this[Taxes.taxPaid] = it.taxPaid
        }.singleOrNull()
    }

    override fun getAll(quoteId: Int): List<Tax> = transaction {
        Taxes.select(Taxes.quoteId eq quoteId).map {
            Taxes.toTax(it)
        }

    }
}