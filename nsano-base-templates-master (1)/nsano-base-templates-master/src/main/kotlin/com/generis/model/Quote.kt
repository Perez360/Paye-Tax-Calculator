package com.generis.com.generis.model

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable


@Serializable
data class QuoteResponseDTO(
    val id: Int? = null,
    var monthlyGrossIncome: Double = 0.00,
    var monthlyAllowances: Double = 0.00,
    var taxRelief: Double = 0.00,
    var monthlyNetIncome: Double,
    var incomeTax: Double,
    var ssnit: Double,
    var taxes: List<Tax>
) {

    fun toSmsString(): String {
        return "ID:                     $id\n" +
                "Monthly Gross Income:  $monthlyGrossIncome\n" +
                "Monthly Allowances:    $monthlyAllowances\n" +
                "Tax Relief:            $taxRelief\n" +
                "Monthly Net Income:    $monthlyNetIncome\n" +
                "SSNIT:                 $ssnit\n" +
                "taxes:                 $taxes\n"
    }


    fun toEmailString(): String {
        return """
                                <h4>Quote</h4>
                ----------------------------------------------<br>
                <div>ID:                     $id</div>
                <div>Monthly Gross Income:   $monthlyGrossIncome</div>
                <div>Monthly Allowances:     $monthlyAllowances</div>
                <div>Tax Relief:             $taxRelief</div>
                <div>Monthly Net Income:     $monthlyNetIncome</div>
                <div>Income Tax:             $incomeTax</div>
                <div>SSNIT:                  $ssnit</div>
                <div>----------------------------------------------</div>
                                <h4>Tax</h4>
                Taxes:<br> 
                $taxes      
              """
    }
}

@Serializable
data class Tax(
    val id: Int? = null,
    val quoteId: Int? = null,
    var taxableAmount: Double,
    var taxRate: Double,
    var taxPaid: Double
)

data class CreateTaxDto(
    var quoteId: Int?=null,
    var taxableAmount: Double,
    var taxRate: Double,
    var taxPaid: Double
)

@Serializable
data class TaxRequest(
    var monthlyGrossIncome: Double,
    var monthlyAllowances: Double,
    var taxRelief: Double
)

data class Quote(
    val id: Int,
    val monthlyGrossIncome: Double,
    val monthlyAllowances: Double,
    val taxRelief: Double,
    val monthlyNetIncome: Double,
    val incomeTax: Double,
    val ssnit: Double,
)

data class CreateQuoteDT0(
    @Required
    var monthlyGrossIncome: Double = 0.00,
    var monthlyAllowances: Double = 0.00,
    var taxRelief: Double = 0.00,
    var monthlyNetIncome: Double,
    var incomeTax: Double,
    var ssnit: Double
)
