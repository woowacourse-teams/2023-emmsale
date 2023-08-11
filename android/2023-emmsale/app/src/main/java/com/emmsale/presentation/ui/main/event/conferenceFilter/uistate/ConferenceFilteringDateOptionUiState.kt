package com.emmsale.presentation.ui.main.event.conferenceFilter.uistate

import android.content.Context
import com.emmsale.R
import java.time.LocalDate

data class ConferenceFilteringDateOptionUiState(
    val date: LocalDate,
) {
    fun transformToDateString(context: Context, isLast: Boolean = false): String = when (isLast) {
        true -> context.getString(
            R.string.conferencefilter_duration_date_last_format,
            date.year % 1000,
            date.monthValue,
            date.dayOfMonth,
        )

        false -> context.getString(
            R.string.conferencefilter_duration_date_format,
            date.year % 1000,
            date.monthValue,
            date.dayOfMonth,
        )
    }
}
