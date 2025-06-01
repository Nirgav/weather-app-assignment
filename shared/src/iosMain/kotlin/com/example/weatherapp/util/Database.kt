package com.example.weatherapp.util

import com.example.weatherapp.MyAppDb
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

fun createDriver(): SqlDriver {
    return NativeSqliteDriver(MyAppDb.Schema, "weather.db")
}