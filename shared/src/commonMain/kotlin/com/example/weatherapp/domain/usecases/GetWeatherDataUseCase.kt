package com.example.weatherapp.domain.usecases

import com.example.weatherapp.Weather
import com.example.weatherapp.domain.core.NetworkResult
import com.example.weatherapp.domain.repository.WeatherRepository
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetWeatherDataUseCase : KoinComponent {
    private val repository: WeatherRepository by inject()

    operator fun invoke(
        latitude: Double,
        longitude: Double,
        forceRefresh: Boolean = false,
    ) = flow<NetworkResult<Weather?>> {
        repository
            .getWeather(
                latitude = latitude,
                longitude = longitude,
                forceRefresh = forceRefresh,
            ).onEach { result ->
                when (result) {
                    is Err -> {
                        emit(NetworkResult.Error(result.error.message))
                    }
                    is Ok -> {
                        emit(NetworkResult.Success(result.value))
                    }
                }
            }.collect()
    }.catch {
        emit(NetworkResult.Error(message = it.message))
    }
}
