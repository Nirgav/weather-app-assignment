package com.example.weatherapp.di

import org.koin.core.module.Module

expect fun platformModule(): Module

expect fun initKoin(context: Any? = null)
