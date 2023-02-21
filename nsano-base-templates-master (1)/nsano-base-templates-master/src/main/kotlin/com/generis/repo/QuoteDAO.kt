package com.generis.com.generis.repo

import com.generis.com.generis.model.*
import org.jetbrains.exposed.sql.ResultRow

interface QuoteDAO {
    fun addQuote(createQuoteDT0: CreateQuoteDT0): Int
    fun getQuote(quoteId: Int): Quote?
    fun delete(id: Int): Int
    fun deleteAll(): Int
}