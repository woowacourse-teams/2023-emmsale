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

fun LocalDateTime.toRelativeTime(
    standardTime: LocalDateTime = LocalDateTime.now(),
    context: Context,
): String {
    val durationHours = abs(Duration.between(standardTime, this).toHours())

    return when {
        year < standardTime.year -> format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))

        durationHours >= MAX_HOUR -> format(DateTimeFormatter.ofPattern("MM.dd"))

        durationHours in MIN_HOUR..MAX_HOUR -> {
            val dateFormatter = DateTimeFormatter.ofPattern(
                context.getString(R.string.before_hour_format, durationHours),
            )
            format(dateFormatter)
        }

        durationHours < MIN_HOUR -> {
            val dateFormatter = DateTimeFormatter.ofPattern(
                context.getString(R.string.before_minute_format, durationHours),
            )
            format(dateFormatter)
        }

        else -> ""
    }
}

fun LocalDateTime.toMessageRelativeTime(
    standardTime: LocalDateTime = LocalDateTime.now(),
    context: Context,
): String {
    if (year < standardTime.year) {
        return format(DateTimeFormatter.ofPattern(context.getString(R.string.year_month_day)))
    }

    if (year == standardTime.year &&
        monthValue <= standardTime.monthValue &&
        dayOfMonth < standardTime.dayOfMonth
    ) {
        return format(DateTimeFormatter.ofPattern(context.getString(R.string.month_day)))
    }

    if (year == standardTime.year &&
        monthValue == standardTime.monthValue &&
        dayOfMonth == standardTime.dayOfMonth &&
        minute != standardTime.minute
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
