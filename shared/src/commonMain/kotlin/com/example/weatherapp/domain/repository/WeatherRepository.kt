package com.example.weatherapp.domain.repository

import com.example.weatherapp.Weather
import com.example.weatherapp.cache.AppDao
import com.example.weatherapp.data.remote.api.WeatherApi
import com.example.weatherapp.data.remote.core.KtResult
import com.example.weatherapp.data.remote.core.getErrorMessage
import com.example.weatherapp.domain.core.catchErr
import com.example.weatherapp.domain.core.catchInternal
import com.example.weatherapp.domain.core.isoStringToEpochMillis
import com.example.weatherapp.domain.model.ApiException
import com.example.weatherapp.domain.model.ApiStatusCode
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.mapEither
import kotlinx.coroutines.flow.flow

class WeatherRepository(
    private val api: WeatherApi,
    private val appDao: AppDao,
) {
    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        forceRefresh: Boolean = false,
    ) = flow<KtResult<Weather?, ApiException>> {
        if (forceRefresh || appDao.getLatest() == null) {
            emit(
                api.getCurrentWeather(latitude, longitude).mapEither(success = { weatherData ->
                    weatherData.currentWeather
                        ?.let {
                            appDao.insertRecent(
                                temperature = it.temperature,
                                windSpeed = it.windspeed,
                                timestamp = isoStringToEpochMillis(it.time.toString()),
                                windDirection = it.winddirection?.toDouble(),
                                is_day = it.isDay?.toDouble(),
                                interval = it.interval?.toDouble(),
                            )
                        }
                    appDao.getLatest()
                }, failure = {
                    ApiException.ServerError(
                        message = getErrorMessage(it),
                        code = ApiStatusCode.getApiCode(it.code),
                    )
                }),
            )
        } else {
            emit(Ok(appDao.getLatest()))
        }
    }.catchInternal {
        emit(it)
    }.catchErr {
        emit(it)
    }
}
