
package com.example.weatherapp.domain.model

sealed class ApiException(
    cause: Throwable?,
    message: String?,
) : Throwable(message, cause) {
    data class ServerError(
        val code: ApiStatusCode? = null,
        override val message: String? = null,
    ) : ApiException(null, message)

    data class Internal(
        override val cause: Throwable? = null,
        override val message: String? = null,
    ) : ApiException(cause, message)

    object OtpException : ApiException(cause = null, message = null)

    object NotWhiteListNumberException : ApiException(cause = null, message = null)

    object NotIdentified : ApiException(null, null)

    data class WrongElementException(
        override val message: String?,
    ) : ApiException(null, message)

    object InternetError : ApiException(null, null)
}
