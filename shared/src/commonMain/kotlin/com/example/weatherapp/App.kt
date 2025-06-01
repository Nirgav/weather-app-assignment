package com.example.weatherapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.Box
import com.example.weatherapp.presentation.weather.WeatherScreen

@Composable
fun App() {
    Scaffold(modifier = Modifier.systemBarsPadding().fillMaxSize(), backgroundColor = Color.White) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            WeatherScreen()
        }
    }
}