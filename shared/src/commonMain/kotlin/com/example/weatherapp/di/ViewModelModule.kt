package com.example.weatherapp.di

import com.example.weatherapp.presentation.weather.WeatherScreenViewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        factory { WeatherScreenViewModel(get(), get()) }
    }
