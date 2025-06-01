package com.example.weatherapp.cache

import com.example.weatherapp.MyAppDb
import com.example.weatherapp.Weather

class AppDao(
    myAppDb: MyAppDb,
) {
    private val dbQuery = myAppDb.weatherQueries

    fun insertRecent(
        temperature: Double?,
        windSpeed: Double?,
        timestamp: Long?,
        windDirection: Double?,
        is_day: Double?,
        interval: Double?,
    ) {
        dbQuery.insertWeather(
            temperature = temperature,
            windSpeed = windSpeed,
            timestamp = timestamp,
            windDirection = windDirection,
            is_day = is_day,
            interval = interval,
        )
    }

    fun getLatest(): Weather? = dbQuery.getLatest().executeAsOneOrNull()
}
