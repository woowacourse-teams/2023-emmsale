package com.emmsale.presentation.ui.main.event.conference.uistate

import android.content.Context
import com.emmsale.R
import java.time.LocalDate

data class ConferenceSelectedFilteringDateOptionUiState(
    val selectedDate: LocalDate,
) {
    fun transformToDateString(context: Context, isLast: Boolean = false): String = when (isLast) {
        true -> context.getString(
            R.string.conferencefilter_duration_date_last_format,
            selectedDate.year % 1000,
            selectedDate.monthValue,
            selectedDate.dayOfMonth,
        )

        false -> context.getString(
            R.string.conferencefilter_duration_date_format,
            selectedDate.year % 1000,
            selectedDate.monthValue,
            selectedDate.dayOfMonth,
        )
    }

    companion object {
        fun from(selectedDate: LocalDate): ConferenceSelectedFilteringDateOptionUiState =
            ConferenceSelectedFilteringDateOptionUiState(
                selectedDate = selectedDate,
            )
    }
}
