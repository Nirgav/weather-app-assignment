package com.example.weatherapp.domain.model

enum class ApiStatusCode(val code: Int) {
    Success(200),
    EmailAlreadyRegistered(409),
    ServerError(500),
    NotFound(404),
    InvalidEntity(422),
    Error401(401);

    companion object {
        fun getApiCode(code: Int?) = when (code) {
            200 -> Success
            409 -> EmailAlreadyRegistered
            404 -> NotFound
            422 -> InvalidEntity
            401 -> Error401
            else -> ServerError
        }
    }
}
