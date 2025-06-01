package com.example.weatherapp.util

import androidx.compose.ui.window.ComposeUIViewController
import com.example.weatherapp.App
import com.example.weatherapp.di.initKoin

fun MainViewController() = ComposeUIViewController {
        initKoin()
        App()
}