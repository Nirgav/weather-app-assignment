package com.example.weatherapp.di

import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.domain.usecases.GetWeatherDataUseCase
import org.koin.dsl.module

val repositoryModule =
    module {
        factory { GetWeatherDataUseCase() }
        single { WeatherRepository(get(), get()) }
    }
