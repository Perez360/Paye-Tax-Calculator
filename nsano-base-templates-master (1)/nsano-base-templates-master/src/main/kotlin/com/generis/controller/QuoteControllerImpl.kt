package com.generis.com.generis.controller

import com.generis.com.generis.model.QuoteResponseDTO
import com.generis.com.generis.model.Tax
import com.generis.com.generis.model.TaxRequest
import com.generis.com.generis.repo.QuoteDAO
import com.generis.com.generis.repo.TaxDAO
import com.generis.com.generis.util.TaxRequestCalcHandler
import com.generis.exceptions.ServiceException
import com.generis.integrations.MailService
import com.generis.integrations.SmsService
import com.generis.model.APIResponse
import com.generis.model.integration.MailRequestDto
import com.generis.model.integration.MailResponseDto
import com.generis.model.integration.SMSRequestDto
import com.generis.model.integration.SMSResponseDto
import com.generis.util.wrapFailureInResponse
import com.generis.util.wrapSuccessInResponse
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.slf4j.LoggerFactory

class QuoteControllerImpl(override val di: DI) : QuoteController, DIAware {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val quoteDAO: QuoteDAO by di.instance()
    private val taxDAO: TaxDAO by di.instance()
    private val mailService: MailService by di.instance()
    private val smsService: SmsService by di.instance()


    override fun createQuote(taxRequest: TaxRequest): APIResponse<List<QuoteResponseDTO>> {


        val taxRequestCalcHandler = TaxRequestCalcHandler()
        val createQuoteDT0 = taxRequestCalcHandler.calculateQuote(taxRequest)

        val savedQuoteId = quoteDAO.addQuote(createQuoteDT0)

        val listOfTaxes = taxRequestCalcHandler.getTaxes(savedQuoteId)
        val savedTaxes = taxDAO.create(listOfTaxes)

        if (savedQuoteId < 1 && savedTaxes == null)
            throw ServiceException(-1, "Could not save quote.")
        logger.info("Saved a quote")

        val savedQuote = quoteDAO.getQuote(savedQuoteId) ?: throw ServiceException(-1, "Could fetch quote after saving")
        val taxes = taxDAO.getAll(savedQuoteId)


        return wrapSuccessInResponse(
            listOf(
                QuoteResponseDTO(
                    id = savedQuote.id,
                    monthlyGrossIncome = savedQuote.monthlyGrossIncome,
                    monthlyAllowances = savedQuote.monthlyAllowances,
                    taxRelief = savedQuote.taxRelief,
                    ssnit = savedQuote.ssnit,
                    incomeTax = savedQuote.incomeTax,
                    monthlyNetIncome = savedQuote.monthlyNetIncome,
                    taxes = taxes
                )
            )
        )

    }

    override fun getQuote(quoteId: Int): APIResponse<List<QuoteResponseDTO>> {
        val quote = quoteDAO.getQuote(quoteId) ?: return wrapFailureInResponse("Quote not found")
        val listOfTaxes = taxDAO.getAll(quoteId)

        logger.info("Found a quote")
        return wrapSuccessInResponse(
            listOf(
                QuoteResponseDTO(
                    id = quote.id,
                    monthlyGrossIncome = quote.monthlyGrossIncome,
                    monthlyAllowances = quote.monthlyAllowances,
                    taxRelief = quote.taxRelief,
                    ssnit = quote.ssnit,
                    incomeTax = quote.incomeTax,
                    monthlyNetIncome = quote.monthlyNetIncome,
                    taxes = listOfTaxes
                )
            )
        )
    }

    override fun getAllTaxes(quoteId: Int): APIResponse<List<Tax>> {
        val taxes = taxDAO.getAll(quoteId)
        if (taxes.isEmpty()) return wrapFailureInResponse("No quotes found")

        logger.info("Found quote(s)")
        return wrapSuccessInResponse(taxes)
    }


    override fun deleteQuotes(quoteId: Int): APIResponse<List<Boolean>> {
        quoteDAO.getQuote(quoteId) ?: return wrapFailureInResponse("No quote found")
        val deletedQuoteCount = quoteDAO.delete(quoteId)

        if (deletedQuoteCount < 1)
            throw ServiceException(-4, "Could not delete quote by id ::: $quoteId")

        return wrapSuccessInResponse(listOf(true))
    }

    override fun deleteAllQuotes(): APIResponse<List<QuoteResponseDTO>> {
        val deleteQuoteCount = quoteDAO.deleteAll()

        if (deleteQuoteCount < 1)
            throw ServiceException(-4, "could not delete all quotes ")

        println(deleteQuoteCount)

        logger.info("Deleted all quotes")
        return wrapSuccessInResponse(listOf())
    }

    override fun sendQuoteAsEmail(mailRequestDto: MailRequestDto): APIResponse<List<MailResponseDto>> {
        val quote = quoteDAO.getQuote(mailRequestDto.quoteId)
            ?: return wrapFailureInResponse("Quote not found")
        val listOfTaxes = taxDAO.getAll(mailRequestDto.quoteId)

        val quoteResponseDTO = QuoteResponseDTO(
            id = mailRequestDto.quoteId,
            monthlyGrossIncome = quote.monthlyGrossIncome,
            monthlyAllowances = quote.monthlyAllowances,
            taxRelief = quote.taxRelief,
            ssnit = quote.ssnit,
            incomeTax = quote.incomeTax,
            monthlyNetIncome = quote.monthlyNetIncome,
            taxes = listOfTaxes
        )

        mailRequestDto.message = quoteResponseDTO.toEmailString()
        return wrapSuccessInResponse(
            listOf(mailService.sendMail(mailRequestDto))
        )
    }

    override fun sendQuoteAsSms(smsRequestDto: SMSRequestDto): APIResponse<List<SMSResponseDto>> {
        val quote = quoteDAO.getQuote(smsRequestDto.quoteId)
            ?: return wrapFailureInResponse("Quote not found")
        val listOfTaxes = taxDAO.getAll(smsRequestDto.quoteId)

        val quoteResponseDTO = QuoteResponseDTO(
            id = smsRequestDto.quoteId,
            monthlyGrossIncome = quote.monthlyGrossIncome,
            monthlyAllowances = quote.monthlyAllowances,
            taxRelief = quote.taxRelief,
            ssnit = quote.ssnit,
            incomeTax = quote.incomeTax,
            monthlyNetIncome = quote.monthlyNetIncome,
            taxes = listOfTaxes
        )

        smsRequestDto.message = quoteResponseDTO.toSmsString()
        return wrapSuccessInResponse(
            listOf(smsService.sendSms(smsRequestDto))
        )
    }

}