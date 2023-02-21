package com.generis.com.generis.controller

import com.generis.com.generis.model.QuoteResponseDTO
import com.generis.com.generis.model.Tax
import com.generis.com.generis.model.TaxRequest
import com.generis.model.APIResponse
import com.generis.model.integration.MailRequestDto
import com.generis.model.integration.MailResponseDto
import com.generis.model.integration.SMSRequestDto
import com.generis.model.integration.SMSResponseDto


interface QuoteController {
    /**
     * Add  a Quote
     * */
    fun createQuote(taxRequest: TaxRequest): APIResponse<List<QuoteResponseDTO>>

    /**
     * Get one quote by id
     * */
    fun getQuote(quoteId: Int): APIResponse<List<QuoteResponseDTO>>

    /**
     * Get all tax on a quote by quotId
     * */
    fun getAllTaxes(quoteId: Int): APIResponse<List<Tax>>

    /**
     * Delete an Quote
     * */
    fun deleteQuotes(quoteId: Int): APIResponse<List<Boolean>>

    /**
     * Delete all quotes
     * */
    fun deleteAllQuotes(): APIResponse<List<QuoteResponseDTO>>

    /**
     * Send quote as Email
     * */
    fun sendQuoteAsEmail(mailRequestDto: MailRequestDto): APIResponse<List<MailResponseDto>>

    /**
     * Send quote as SMS
     * */
    fun sendQuoteAsSms(smsRequestDto: SMSRequestDto): APIResponse<List<SMSResponseDto>>
}