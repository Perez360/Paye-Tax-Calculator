package com.generis.com.generis.repo

import com.generis.com.generis.model.CreateQuoteDT0
import com.generis.com.generis.model.Quote
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class QuoteService(override val di: DI) : QuoteDAO, DIAware {
    private val taxDAO: TaxDAO by di.instance()

    override fun addQuote(createQuoteDT0: CreateQuoteDT0): Int = transaction {
        Quotes.insert {
            it[monthlyGrossIncome] = createQuoteDT0.monthlyGrossIncome
            it[monthlyAllowances] = createQuoteDT0.monthlyAllowances
            it[taxRelief] = createQuoteDT0.taxRelief
            it[monthlyNetIncome] = createQuoteDT0.monthlyNetIncome
            it[incomeTax] = createQuoteDT0.incomeTax
            it[ssnit] = createQuoteDT0.ssnit
        } get Quotes.id
    }


    override fun getQuote(quoteId: Int): Quote? = transaction {
        Quotes.select(Quotes.id eq quoteId).map {
            Quote(
                id = it[Quotes.id],
                monthlyGrossIncome = it[Quotes.monthlyGrossIncome],
                monthlyAllowances = it[Quotes.monthlyAllowances],
                taxRelief = it[Quotes.taxRelief],
                ssnit = it[Quotes.ssnit],
                monthlyNetIncome = it[Quotes.monthlyNetIncome],
                incomeTax = it[Quotes.incomeTax],
            )
        }.singleOrNull()
    }


    override fun delete(id: Int): Int = transaction {
        Quotes.deleteWhere { Quotes.id eq id }
    }

    override fun deleteAll(): Int = transaction {
        Quotes.deleteAll()
    }
}
