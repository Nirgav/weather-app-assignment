package com.example.weatherapp.di

import com.example.weatherapp.MyAppDb
import com.example.weatherapp.util.createDriver
import org.koin.core.context.startKoin
import org.koin.dsl.module

actual fun platformModule() = module {
    single { createDriver() }
    single { MyAppDb(get()) }
}

actual fun initKoin(context: Any?) {
    startKoin {
        modules(getShareModules())
    }
}