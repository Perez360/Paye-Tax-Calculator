package com.generis.com.generis.repo

import com.generis.com.generis.model.CreateTaxDto
import com.generis.com.generis.model.Tax
import org.jetbrains.exposed.sql.ResultRow

interface TaxDAO {
    fun create(listOfTaxes: List<CreateTaxDto>): ResultRow?
    fun getAll(quoteId: Int): List<Tax>
}