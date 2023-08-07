package com.emmsale.presentation.utils

import android.content.Context
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.emmsale.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@BindingAdapter(
    "app:startEventDateTime",
    "app:endEventDateTime",
    requireAll = true,
)
fun TextView.setEventDuration(
    startLocalDateTime: LocalDateTime,
    endLocalDateTime: LocalDateTime,
) {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    val eventStartDate = startLocalDateTime.format(dateTimeFormatter)
    val eventEndDate = endLocalDateTime.format(dateTimeFormatter)

    text = context.parseEventDuration(eventStartDate, eventEndDate)
}

fun Context.parseEventDuration(eventStartDate: String, eventEndDate: String) =
    when (eventStartDate == eventEndDate) {
        true -> getString(R.string.event_start_end_duration_format)
            .format(eventStartDate, eventEndDate)

        false -> getString(R.string.event_start_duration_format)
            .format(eventStartDate)
    }
