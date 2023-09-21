package com.emmsale.presentation.common.bindingadapter

import android.content.Context
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.emmsale.R
import com.emmsale.presentation.common.extension.format
import com.emmsale.presentation.common.extension.toMessageRelativeTime
import com.emmsale.presentation.common.extension.toRelativeTime
import java.time.LocalDateTime

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
            return localDateTime.format(context, R.string.month_day_weekday)
        }
    },
    MONTH_DOT_DAY {
        override fun format(context: Context, localDateTime: LocalDateTime): String {
            return localDateTime.format(context, R.string.month_day_weekday)
        }
    },
    RELATIVE_TIME {
        override fun format(context: Context, localDateTime: LocalDateTime): String {
            return localDateTime.toRelativeTime(context)
        }
    },
    AM_PM_HOUR_MINUTE {
        override fun format(context: Context, localDateTime: LocalDateTime): String {
            return localDateTime.toMessageRelativeTime(context)
        }
    },
    YEAR_MONTH_DAY_WEEKDAY {
        override fun format(context: Context, localDateTime: LocalDateTime): String {
            return localDateTime.format(context, R.string.year_month_day_weekday)
        }
    },
    ;

    abstract fun format(context: Context, localDateTime: LocalDateTime): String
}
