@file:JvmName("LocalDateTimeExt")

package com.emmsale.presentation.common.extension

import android.content.Context
import com.emmsale.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.compareToString(context: Context): String {
    val currentDateTime = LocalDateTime.now()

    if (year < currentDateTime.year) {
        return format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
    }

    if (year == currentDateTime.year &&
        monthValue <= currentDateTime.monthValue &&
        dayOfMonth < currentDateTime.dayOfMonth
    ) {
        return format(DateTimeFormatter.ofPattern("MM.dd"))
    }

    if (year == currentDateTime.year &&
        monthValue == currentDateTime.monthValue &&
        dayOfMonth == currentDateTime.dayOfMonth &&
        hour != currentDateTime.hour
    ) {
        return format(DateTimeFormatter.ofPattern(context.getString(R.string.primarynotification_before_hour_format)))
    }

    if (year == currentDateTime.year &&
        monthValue == currentDateTime.monthValue &&
        dayOfMonth == currentDateTime.dayOfMonth &&
        hour == currentDateTime.hour
    ) {
        return format(DateTimeFormatter.ofPattern(context.getString(R.string.primarynotification_before_minute_format)))
    }

    return ""
}
