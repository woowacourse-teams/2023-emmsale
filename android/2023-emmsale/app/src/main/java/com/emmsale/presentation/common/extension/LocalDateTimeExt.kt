@file:JvmName("LocalDateTimeExt")

package com.emmsale.presentation.common.extension

import android.content.Context
import com.emmsale.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.toRelativeTimeText(context: Context): String {
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
        val dateFormatter = DateTimeFormatter.ofPattern(
            context.getString(
                R.string.before_hour_format,
                currentDateTime.hour.minus(hour),
            ),
        )
        return format(dateFormatter)
    }

    if (year == currentDateTime.year &&
        monthValue == currentDateTime.monthValue &&
        dayOfMonth == currentDateTime.dayOfMonth &&
        hour == currentDateTime.hour
    ) {
        val dateFormatter = DateTimeFormatter.ofPattern(
            context.getString(
                R.string.before_minute_format,
                currentDateTime.minute.minus(minute),
            ),
        )
        return format(dateFormatter)
    }

    return ""
}
