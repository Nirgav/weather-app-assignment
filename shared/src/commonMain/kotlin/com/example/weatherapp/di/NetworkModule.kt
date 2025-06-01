package com.example.weatherapp.di

import com.example.weatherapp.data.remote.api.WeatherApi
import com.example.weatherapp.domain.repository.WeatherRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule =
    module {
        single { createHttpClient() }
        factory { WeatherApi(get()) }
        single { WeatherRepository(get(), get()) }
    }

fun createHttpClient(): HttpClient =
    HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
    }

private val sharedModule =
    listOf(networkModule, platformModule(), databaseModule, repositoryModule, viewModelModule)

fun getShareModules() = sharedModule
