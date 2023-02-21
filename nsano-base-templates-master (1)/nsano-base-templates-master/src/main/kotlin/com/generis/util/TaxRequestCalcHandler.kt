package com.generis.com.generis.util

import com.generis.com.generis.model.CreateQuoteDT0
import com.generis.com.generis.model.CreateTaxDto
import com.generis.com.generis.model.TaxRequest


class TaxRequestCalcHandler {
    private val listOfTaxes = mutableListOf<CreateTaxDto>()
    private val ssnitRate = 0.055
    private val chargeableIncomes: Array<Double> = arrayOf(365.00, 110.00, 130.00, 3000.00, 16395.00, 20000.00)
    private val taxRates: Array<Double> = arrayOf(0.00, 5.00, 10.00, 17.50, 25.00, 30.00)
    private var taxableIncome: Double = 0.00

    //Remainder for holds the new taxable income after chargeable income has been deducted from the old taxable income
    private var remainder = 0.00

    //For saving the total tax paid at each boundary (chargeable income)
    private var incomeTax = 0.00

    //for holding the tax paid at a particular boundary (chargeable income)
    private var taxPaid = 0.00


    fun calculateQuote(taxRequest: TaxRequest): CreateQuoteDT0 {
        val snnit = ssnitRate * taxRequest.monthlyGrossIncome
        taxableIncome = (taxRequest.monthlyGrossIncome - snnit) + taxRequest.monthlyAllowances - taxRequest.taxRelief
        remainder = taxableIncome

        for ((count, chargeableIncome) in chargeableIncomes.withIndex()) {
            if (remainder > 0) {
                if (remainder >= chargeableIncome) {

                    // If chargeable income is greater or equal to 20000, tax paid on the
                    // remaining taxable income =remainder * the tax rate on the chargeable income (20000)
                    if (chargeableIncome >= 20000.00) {

                        incomeTax += remainder * (taxRates[count] / 100)
                        taxPaid = String.format("%.2f", remainder * (taxRates[count] / 100)).toDouble()
                        listOfTaxes.add(
                            CreateTaxDto(
                                taxableAmount = "%.0f".format(remainder).toDouble(),
                                taxRate = taxRates[count],
                                taxPaid = taxPaid
                            )
                        )
                    } else {

                        remainder -= chargeableIncome
                        incomeTax += chargeableIncome * (taxRates[count] / 100)
                        taxPaid = String.format("%.2f", chargeableIncome * (taxRates[count] / 100)).toDouble()
                        listOfTaxes.add(
                            CreateTaxDto(
                                taxableAmount = "%.0f".format(chargeableIncome).toDouble(),
                                taxRate = taxRates[count],
                                taxPaid = taxPaid

                            )
                        )
                    }
                } else {
                    incomeTax += remainder * (taxRates[count] / 100)
                    taxPaid = String.format("%.2f", remainder * (taxRates[count] / 100)).toDouble()
                    listOfTaxes.add(
                        CreateTaxDto(
                            taxableAmount = "%.0f".format(remainder).toDouble(),
                            taxRate = taxRates[count],
                            taxPaid = taxPaid
                        )
                    )
                    remainder = 0.00
                }

            } else {
                break
            }

        }

        val monthlyNetIncome = (taxRequest.monthlyGrossIncome + taxRequest.monthlyAllowances) - snnit - incomeTax

        return CreateQuoteDT0(
            monthlyGrossIncome = taxRequest.monthlyGrossIncome,
            monthlyAllowances = taxRequest.monthlyAllowances,
            taxRelief = taxRequest.taxRelief,
            monthlyNetIncome = String.format("%.2f", monthlyNetIncome).toDouble(),
            incomeTax = String.format("%.2f", incomeTax).toDouble(),
            ssnit = String.format("%.2f", snnit).toDouble(),
        )
    }


    fun getTaxes(quoteId: Int): List<CreateTaxDto> {
        listOfTaxes.map {
            it.quoteId = quoteId
        }
        return listOfTaxes
    }
}