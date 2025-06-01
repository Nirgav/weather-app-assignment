package com.example.weatherapp.db

import android.content.Context
import com.example.weatherapp.MyAppDb
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

fun createDriver(context: Context): SqlDriver = AndroidSqliteDriver(MyAppDb.Schema, context, "weather.db")
