@file:JvmName("LocalDateTimeExt")

package com.emmsale.presentation.common.extension

import android.content.Context
import androidx.annotation.StringRes
import com.emmsale.R
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

private const val MIN_HOUR = 1
private const val MAX_HOUR = 24
private const val MAX_MINUTE = 60

fun LocalDateTime.toRelativeTime(context: Context): String {
    val currentDateTime = LocalDateTime.now()
    val duration = Duration.between(currentDateTime, this)

    if (year < currentDateTime.year) {
        return format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
    }

    if (year == currentDateTime.year &&
        monthValue <= currentDateTime.monthValue &&
        dayOfMonth < currentDateTime.dayOfMonth
    ) {
        return format(DateTimeFormatter.ofPattern("MM.dd"))
    }

    if (duration.toHours() in MIN_HOUR..MAX_HOUR) {
        val dateFormatter = DateTimeFormatter.ofPattern(
            context.getString(
                R.string.before_hour_format,
                abs(duration.toHours()),
            ),
        )
        return format(dateFormatter)
    }

    if (duration.toHours() < MIN_HOUR) {
        val dateFormatter = DateTimeFormatter.ofPattern(
            context.getString(
                R.string.before_minute_format,
                abs(duration.toMinutes()),
            ),
        )
        return format(dateFormatter)
    }

    return ""
}

fun LocalDateTime.toMessageRelativeTime(context: Context): String {
    val currentDateTime = LocalDateTime.now()

    if (year < currentDateTime.year) {
        return format(DateTimeFormatter.ofPattern(context.getString(R.string.year_month_day)))
    }

    if (year == currentDateTime.year &&
        monthValue <= currentDateTime.monthValue &&
        dayOfMonth < currentDateTime.dayOfMonth
    ) {
        return format(DateTimeFormatter.ofPattern(context.getString(R.string.month_day)))
    }

    if (year == currentDateTime.year &&
        monthValue == currentDateTime.monthValue &&
        dayOfMonth == currentDateTime.dayOfMonth &&
        minute != currentDateTime.minute
    ) {
        val dateFormatter = DateTimeFormatter.ofPattern(
            context.getString(R.string.am_pm_hour_minute),
        )
        return format(dateFormatter)
    }

    return context.getString(R.string.all_just_before)
}

fun LocalDateTime.format(
    context: Context,
    @StringRes patternStringResId: Int,
): String {
    val pattern = context.getString(patternStringResId)
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    return formatter.format(this)
}
