package com.example.weatherapp.di

import android.content.Context
import com.example.weatherapp.MyAppDb
import com.example.weatherapp.db.createDriver
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

actual fun platformModule() =
    module {
        single { createDriver(get()) }
        single { MyAppDb(get()) }
    }

actual fun initKoin(context: Any?) {
    require(context is Context) { "Expected Android Context but got $context" }

    startKoin {
        androidContext(context)
        modules(getShareModules())
    }
}
