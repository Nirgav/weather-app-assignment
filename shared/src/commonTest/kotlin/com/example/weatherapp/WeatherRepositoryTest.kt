package com.example.weatherapp

import app.cash.turbine.test
import com.example.weatherapp.cache.AppDao
import com.example.weatherapp.data.remote.api.WeatherApi
import com.example.weatherapp.data.remote.core.ApiError
import com.example.weatherapp.data.remote.dto.response.CurrentWeather
import com.example.weatherapp.data.remote.dto.response.WeatherResponse
import com.example.weatherapp.domain.model.ApiException
import com.example.weatherapp.domain.model.ApiStatusCode
import com.example.weatherapp.domain.repository.WeatherRepository
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WeatherRepositoryTest {
    private val api = mockk<WeatherApi>()
    private val dao = mockk<AppDao>(relaxed = true)
    private lateinit var repository: WeatherRepository

    @BeforeTest
    fun setup() {
        repository = WeatherRepository(api, dao)
    }

    @Test
    fun `returns weather from API and stores in DB when forceRefresh is true`() =
        runTest {
            val mockWeather =
                WeatherResponse(
                    CurrentWeather(
                        temperature = 10.0,
                        windspeed = 5.0,
                        time = "1627880400",
                    ),
                )
            coEvery { api.getCurrentWeather(any(), any()) } returns Ok(mockWeather)

            repository.getWeather(10.0, 20.0, forceRefresh = true).test {
                val result = awaitItem()
                assertTrue(result is Ok)
                coVerify { dao.insertRecent(10.0, 5.0, 1627880400L) }
                awaitComplete()
            }
        }

    @Test
    fun `returns cached weather when forceRefresh is false and cache exists`() =
        runTest {
            val cachedWeather = Weather(0, 22.0, 3.0, 12345678L)
            coEvery { dao.getLatest() } returns cachedWeather

            repository.getWeather(10.0, 20.0, forceRefresh = false).test {
                val result = awaitItem()
                assertTrue(result is Ok && result.value == cachedWeather)
                awaitComplete()
            }
        }

    @Test
    fun `returns error when API fails`() =
        runTest {
            coEvery { dao.getLatest() } returns null
            coEvery { api.getCurrentWeather(any(), any()) } returns
                Err(
                    ApiError(
                        500,
                        error = "Server error",
                    ),
                )

            repository.getWeather(10.0, 20.0, forceRefresh = true).test {
                val result = awaitItem()
                assertTrue(result is Err)
                val apiError = result.error as ApiException.ServerError
                assertEquals(apiError.code, ApiStatusCode.ServerError)
                awaitComplete()
            }
        }
}
