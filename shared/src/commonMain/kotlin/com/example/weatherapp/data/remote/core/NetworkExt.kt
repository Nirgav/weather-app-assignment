package com.example.weatherapp.data.remote.core

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

suspend inline fun <reified T, reified E> HttpResponse.getResponse(): Result<T, ApiError<E>> =
    if (status.isSuccess()) {
        Ok(body())
    } else {
        Err(ApiError(status.value, body<E>()))
    }

fun getErrorMessage(apiError: ApiError<ApiError<Unit>>): String = apiError.data?.message ?: apiError.data?.error ?: ""
