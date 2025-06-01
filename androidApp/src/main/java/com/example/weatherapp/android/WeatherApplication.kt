package com.example.weatherapp.android

import android.app.Application
import com.example.weatherapp.di.initKoin

class WeatherApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(this)
    }
}
