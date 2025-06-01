package com.example.weatherapp.data.remote.core

import kotlinx.serialization.Serializable

@Serializable
data class ApiError<T>(
    val code: Int? = null,
    val data: T? = null,
    val success: Boolean? = false,
    val message: String? = null,
    val error: String? = null,
    val detail: String? = null,
    val status_code: Int? = null,
    val exception: String? = null,
    val duration: String? = null,
    val more_info: String? = null,
)
