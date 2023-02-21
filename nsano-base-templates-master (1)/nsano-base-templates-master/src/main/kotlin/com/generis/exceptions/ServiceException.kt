package com.generis.exceptions


import io.ktor.http.*

class ServiceException(override val message: String?, val code:Int, override val cause: Throwable?) : RuntimeException(message, cause) {
    constructor(code: Int, message: String?) : this(message, code, null)

    constructor(code: Int, message: String?,cause: Throwable?) : this(message, code, cause)

    constructor(cause: Throwable?) : this(cause?.toString(), HttpStatusCode.InternalServerError.value, cause)

    constructor() : this("We really fucked up", HttpStatusCode.InternalServerError.value, null)
}
