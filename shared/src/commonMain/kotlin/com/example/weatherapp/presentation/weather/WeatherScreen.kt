package com.example.weatherapp.presentation.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.Weather
import com.example.weatherapp.domain.core.formatTimestamp
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory

@Composable
fun WeatherScreen() {
    val factory: PermissionsControllerFactory = rememberPermissionsControllerFactory()
    val controller: PermissionsController =
        remember(factory) { factory.createPermissionsController() }

    BindEffect(controller)
    val viewModel = remember { WeatherScreenViewModel(permissionsController = controller) }
    val uiState = viewModel.uiState.collectAsState().value
    LaunchedEffect(Unit) {
        viewModel.fetchWeatherData()
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (val state = uiState) {
            is WeatherUiState.Loading -> {
                CircularProgressIndicator(
                    color = Color.Black,
                )
            }

            is WeatherUiState.Success -> {
                // Display weather data
                WeatherContent(weather = state.weather)
            }

            is WeatherUiState.Error -> {
                ErrorState(
                    message = state.message,
                    onRetry = {
                        if (state.retryable) {
                            viewModel.fetchWeatherData()
                        }
                    },
                )
            }

            is WeatherUiState.PermissionRequired -> {
                PermissionRequiredState(
                    permissionMessage = state.message,
                    permissionState = state.permissionState,
                    showRationale = state.showRationale,
                    onRequestPermission = {
                        if (state.permissionState == PermissionState.DeniedForever) {
                            viewModel.openSettings()
                        } else {
                            viewModel.retry()
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun WeatherContent(weather: Weather) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF0F0F0)) // Light background
                    .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color.White,
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "Weather Info",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                    )

                    WeatherItem(label = "Temperature", value = "${weather.temperature ?: "N/A"} °C")
                    WeatherItem(label = "Wind Speed", value = "${weather.windSpeed ?: "N/A"} km/h")
                    WeatherItem(label = "Timestamp", value = formatTimestamp(weather.timestamp ?: 0L))
                    WeatherItem(label = "Wind Direction", value = (weather.windDirection ?: "N/A").toString())
                    WeatherItem(label = "Is Day", value = "${weather.is_day ?: "N/A"}")
                    WeatherItem(label = "Interval", value = "${weather.interval ?: "N/A"}")
                }
            }
        }
    }
}

@Composable
fun WeatherItem(
    label: String,
    value: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        backgroundColor = Color(0xFFF9F9F9),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF555555),
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF222222),
            )
        }
    }
}

@Composable
private fun PermissionRequiredState(
    permissionState: PermissionState,
    permissionMessage: String? = null,
    showRationale: Boolean,
    onRequestPermission: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp),
    ) {
        Text(
            text =
                when (permissionState) {
                    PermissionState.DeniedForever -> permissionMessage.toString()
                    else -> "Location permission required for weather data"
                },
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (PermissionState.DeniedForever != permissionState) {
            Button(onClick = onRequestPermission) {
                Text("Grant Permission")
            }
        }

        if (showRationale) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "We need your location to show weather information for your area",
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp),
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.error,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
