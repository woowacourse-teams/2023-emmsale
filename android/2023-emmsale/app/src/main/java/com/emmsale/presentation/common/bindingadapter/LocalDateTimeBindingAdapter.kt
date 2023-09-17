package com.emmsale.presentation.common.bindingadapter

import android.content.Context
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.emmsale.R
import com.emmsale.presentation.common.extension.toRelativeTimeText
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@BindingAdapter("app:dateText", "app:dateTimeFormatter", requireAll = false)
fun TextView.setDate(
    localDateTime: LocalDateTime? = null,
    dateTimePattern: DateTimePattern = DateTimePattern.MONTH_DAY_WEEKDAY,
) {
    if (localDateTime == null) return

    val dateTimePattern = DateTimePattern.getPattern(context, dateTimePattern)
    val dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern, Locale.getDefault())
    text = dateTimeFormatter.format(localDateTime)
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

    val pattern = DateTimePattern.getPattern(context, dateTimePattern)
    val dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    text = context.getString(
        R.string.date_range,
        dateTimeFormatter.format(startDateTime),
        dateTimeFormatter.format(endDateTime),
    )
}

@BindingAdapter(
    "app:relativeDate",
)
fun TextView.setRelativeTime(
    time: LocalDateTime? = null,
) {
    text = time?.toRelativeTimeText(context)
}

enum class DateTimePattern {
    MONTH_DAY_WEEKDAY,
    ;

    companion object {
        fun getPattern(
            context: Context,
            dateTimePattern: DateTimePattern,
        ): String = when (dateTimePattern) {
            MONTH_DAY_WEEKDAY -> context.getString(R.string.month_day_weekday)
        }
    }
}
