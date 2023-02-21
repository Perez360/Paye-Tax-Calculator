package com.generis.config.plugins


import com.generis.domain.CODE_FAILURE
import com.generis.domain.CODE_SERVICE_FAILURE
import com.generis.exceptions.ServiceException
import com.generis.model.ErrorResponse
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import org.slf4j.LoggerFactory
import java.sql.SQLException
import java.time.format.DateTimeParseException
import javax.validation.ConstraintViolationException

fun Application.configureExceptions() {
    val logger = LoggerFactory.getLogger(this::class.java)

    install(StatusPages) {

        exception<NumberFormatException> { cause ->
            val message = cause.message?: "n/a"
            val errorResponse = ErrorResponse(CODE_SERVICE_FAILURE, CODE_FAILURE, message)

            logger.error("NumberFormatException Error ::: " + cause.stackTraceToString())
            call.respond(
                message = errorResponse,
                status = HttpStatusCode.BadRequest
            )
        }

        exception<DateTimeParseException> { cause ->
            val message = cause.message?: "n/a"
            val errorResponse = ErrorResponse(CODE_SERVICE_FAILURE, CODE_FAILURE, message)

            logger.error("DateTimeParseException Error ::: " + cause.stackTraceToString())
            call.respond(
                message = errorResponse,
                status = HttpStatusCode.BadRequest
            )
        }
        exception<NullPointerException> { cause ->
            val message = cause.message?: "n/a"
            val errorResponse = ErrorResponse(CODE_SERVICE_FAILURE, CODE_FAILURE, message)

            logger.error("NullPointerException Error ::: " + cause.stackTraceToString())
            call.respond(
                message = errorResponse,
                status = HttpStatusCode.InternalServerError
            )
        }

        exception<SQLException> { cause ->

            val message = cause.message?: "n/a"
            val errorResponse = ErrorResponse(CODE_SERVICE_FAILURE, CODE_FAILURE, message)

            logger.error("NullPointerException Error ::: " + cause.stackTraceToString())
            call.respond(
                message = errorResponse,
                status = HttpStatusCode.InternalServerError
            )
        }

        exception<IllegalArgumentException> { cause ->
            val message = cause.message?: "n/a"
            val errorResponse = ErrorResponse(CODE_SERVICE_FAILURE, CODE_FAILURE, message)

            logger.error("IllegalArgumentException Error ::: " + cause.stackTraceToString())

            call.respond(
                message = errorResponse,
                status = HttpStatusCode.BadRequest
            )
        }

        exception<ConstraintViolationException> { cause ->
            val message = cause.message?: "n/a"
            val errorResponse = ErrorResponse(CODE_SERVICE_FAILURE, CODE_FAILURE, message)

            logger.error("ConstraintViolationException Error ::: " + cause.stackTraceToString())
            call.respond(
                message = errorResponse,
                status = HttpStatusCode.Conflict
            )
        }


        exception<ServiceException> { cause ->

            val message = cause.message?: "n/a"
            val errorResponse = ErrorResponse(CODE_SERVICE_FAILURE, CODE_FAILURE, message)
            var httpStatusCode = HttpStatusCode.InternalServerError

            if (cause.code == 0)
                httpStatusCode = HttpStatusCode.OK

            if (cause.code == -2)
                httpStatusCode = HttpStatusCode.BadRequest

            if (cause.code == -3)
                httpStatusCode = HttpStatusCode.NotImplemented

            if (cause.code == -4)
                httpStatusCode = HttpStatusCode.NotModified

            if (cause.code == -5)
                httpStatusCode = HttpStatusCode.FailedDependency

            if (cause.code == -6)
                httpStatusCode = HttpStatusCode.Unauthorized


            logger.error("ServiceException Error ::: " + cause.stackTraceToString())
            call.respond(
                message = errorResponse,
                status = httpStatusCode
            )
        }
    }
}

