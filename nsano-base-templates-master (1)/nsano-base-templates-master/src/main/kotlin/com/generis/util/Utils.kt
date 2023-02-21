package com.generis.util

import com.generis.domain.*
import com.generis.model.APIResponse


fun <T> wrapSuccessInResponse(data: T): APIResponse<T> {
    if (UserRequestContext.getCurrentLanguage() == "fr")
        return APIResponse(CODE_SERVICE_SUCCESS, CODE_SUCCESS, "Succès", data)

    return APIResponse(CODE_SERVICE_SUCCESS, CODE_SUCCESS, "Success", data)
}


fun <T> wrapFailureInResponse(message: String): APIResponse<List<T>> {
    return APIResponse(CODE_SERVICE_FAILURE, CODE_FAILURE, message, listOf())
}





