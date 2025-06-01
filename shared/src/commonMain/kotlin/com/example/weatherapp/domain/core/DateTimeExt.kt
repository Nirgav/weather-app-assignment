package com.example.weatherapp.domain.core

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun isoStringToEpochMillis(isoString: String): Long {
    val localDateTime = LocalDateTime.parse(isoString) // Parses "2025-06-01T11:00"
    val instant = localDateTime.toInstant(TimeZone.UTC) // or use user's time zone
    return instant.toEpochMilliseconds()
}

fun formatTimestamp(timestampMillis: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestampMillis)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault()) // or TimeZone.UTC

    val month = localDateTime.monthNumber.toString().padStart(2, '0')
    val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
    val year = (localDateTime.year % 100).toString().padStart(2, '0')
    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')
    val second = localDateTime.second.toString().padStart(2, '0')

    return "$month/$day/$year $hour:$minute:$second"
}
