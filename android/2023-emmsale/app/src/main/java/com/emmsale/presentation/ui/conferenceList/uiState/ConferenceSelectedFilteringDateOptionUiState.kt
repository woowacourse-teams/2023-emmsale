package com.emmsale.presentation.ui.conferenceList.uiState

import android.content.Context
import com.emmsale.R
import java.time.LocalDate

data class ConferenceSelectedFilteringDateOptionUiState(
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

    companion object {
        fun from(selectedDate: LocalDate): ConferenceSelectedFilteringDateOptionUiState =
            ConferenceSelectedFilteringDateOptionUiState(
                date = selectedDate,
            )
    }
}
