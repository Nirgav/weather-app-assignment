package com.example.weatherapp.di

import com.example.weatherapp.cache.AppDao
import org.koin.dsl.module

val databaseModule =
    module {
        single { AppDao(get()) }
    }
