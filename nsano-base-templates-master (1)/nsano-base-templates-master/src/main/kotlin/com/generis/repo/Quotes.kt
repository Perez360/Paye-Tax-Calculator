package com.generis.com.generis.repo

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table


object Quotes : Table(name = "quotes") {
    val id: Column<Int> = integer("id").autoIncrement().uniqueIndex()
    val monthlyGrossIncome: Column<Double> = double("monthlyGrossIncome").index()
    val monthlyAllowances: Column<Double> = double("monthlyAllowances").index()
    val taxRelief: Column<Double> = double("taxRelief")
    val monthlyNetIncome: Column<Double> = double("monthlyNetIncome").index()
    val incomeTax: Column<Double> = double("monthlyIncomeTax").index()
    val ssnit: Column<Double> = double("ssnit")



    override val primaryKey = PrimaryKey(id, name = "PK_Quote_ID")

}