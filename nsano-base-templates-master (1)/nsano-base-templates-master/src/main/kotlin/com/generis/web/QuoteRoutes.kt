package com.generis.com.generis.web

import com.generis.com.generis.controller.QuoteController
import com.generis.com.generis.model.TaxRequest
import com.generis.model.integration.MailRequestDto
import com.generis.model.integration.SMSRequestDto
import com.generis.util.wrapFailureInResponse
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI


private const val BASE_URL = "/api/v1/tax-quote"
fun Application.payeRoute() {


    routing {
        route(BASE_URL) {
            post("/create") {
                val quoteController by closestDI().instance<QuoteController>()

                val taxRequestDto = call.receive<TaxRequest>()
                call.respond(quoteController.createQuote(taxRequestDto))
            }

            get("/get-quote/id/{id}") {
                val quoteId = call.parameters["id"] ?: throw BadRequestException(message = "Quote id is undefined")

                val quoteController by closestDI().instance<QuoteController>()
                val oneQuote = quoteController.getQuote(quoteId.toInt())
                call.respond(oneQuote)
            }

            get("/getAll-taxes/{id}") {
                val quoteId = call.parameters["id"] ?: throw BadRequestException(message = "Quote id is undefined")
                val quoteController by closestDI().instance<QuoteController>()
                val taxes = quoteController.getAllTaxes(quoteId.toInt())
                call.respond(taxes)
            }



            delete("/delete-quote/{id}") {
                val id = call.parameters["id"] ?: return@delete call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = wrapFailureInResponse<String>("Quote id is undefined")
                )

                val quoteId = Integer.parseInt(id)
                val quoteController by closestDI().instance<QuoteController>()
                call.respond(
                    status = HttpStatusCode.OK,
                    quoteController.deleteQuotes(quoteId)
                )
            }


            post("/send-email") {

                val requestParams = call.receiveParameters()
                val quoteId = requestParams["id"]!!.toInt()
                val subject = requestParams["subject"]!!
                val message = requestParams["message"]!!
                val recipients = requestParams["recipients"]!!
                val signature = requestParams["signature"]!!
                val senderName = requestParams["senderName"]!!


                val quoteController by closestDI().instance<QuoteController>()
                call.respond(
                    quoteController.sendQuoteAsEmail(
                        MailRequestDto(
                            quoteId = quoteId,
                            subject = subject,
                            recipients = recipients,
                            signature = signature,
                            senderName = senderName,
                            message = message,
                        )
                    )
                )
            }

            post("/send-sms") {
                val requestParams = call.receiveParameters()
                val quoteId = requestParams["id"]!!.toInt()
                val message = requestParams["message"]!!
                val sender = requestParams["sender"]!!
                val recipients = requestParams["recipients"]!!


                val quoteController by closestDI().instance<QuoteController>()
                call.respond(
                    quoteController.sendQuoteAsSms(
                        SMSRequestDto(
                            quoteId = quoteId,
                            sender = sender,
                            recipient = recipients,
                            message = message,
                        )
                    )
                )
            }

            delete("/delete/all") {
                val quoteController by closestDI().instance<QuoteController>()
                call.respond(
                    status = HttpStatusCode.OK,
                    quoteController.deleteAllQuotes()
                )
            }
        }
    }

}