package com.emmsale.presentation.common.bindingadapter

import android.content.Context
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.emmsale.R
import com.emmsale.presentation.common.extension.compareToString
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@BindingAdapter("app:dateText", "app:dateTimeFormatter", requireAll = false)
fun TextView.setDate(
    localDateTime: LocalDateTime? = null,
    dateTimePattern: DateTimePattern = DateTimePattern.MONTH_DAY_WEEKDAY,
) {
    if (localDateTime == null) return

    text = dateTimePattern.format(context, localDateTime)
}

@BindingAdapter(
    "app:startDateTime",
    "app:endDateTime",
    "app:dateTimeFormatter",
    requireAll = false,
)
fun TextView.setDateRange(
    startDateTime: LocalDateTime? = null,
    endDateTime: LocalDateTime? = null,
    dateTimePattern: DateTimePattern = DateTimePattern.MONTH_DAY_WEEKDAY,
) {
    if (endDateTime == null) {
        setDate(startDateTime, dateTimePattern)
        return
    }
    if (startDateTime == null) return

    text = context.getString(
        R.string.date_range,
        dateTimePattern.format(context, startDateTime),
        dateTimePattern.format(context, endDateTime),
    )
}

enum class DateTimePattern {
    MONTH_DAY_WEEKDAY {
        override fun format(context: Context, localDateTime: LocalDateTime): String {
            val pattern = context.getString(R.string.month_day_weekday)
            val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
            return formatter.format(localDateTime)
        }
    },
    MONTH_DOT_DAY {
        override fun format(context: Context, localDateTime: LocalDateTime): String {
            val pattern = context.getString(R.string.month_dot_day)
            val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
            return formatter.format(localDateTime)
        }
    },
    YEAR_DOT_MONTH_DOT_DAY {
        override fun format(context: Context, localDateTime: LocalDateTime): String {
            val pattern = context.getString(R.string.year_dot_month_dot_day)
            val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
            return formatter.format(localDateTime)
        }
    },
    RELATED_TIME {
        override fun format(context: Context, localDateTime: LocalDateTime): String {
            return localDateTime.compareToString(context)
        }
    },
    ;

    abstract fun format(context: Context, localDateTime: LocalDateTime): String
}
