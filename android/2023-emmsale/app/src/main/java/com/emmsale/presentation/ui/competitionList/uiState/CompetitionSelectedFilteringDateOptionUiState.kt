package com.emmsale.presentation.ui.competitionList.uiState

import android.content.Context
import com.emmsale.R
import java.time.LocalDate

data class CompetitionSelectedFilteringDateOptionUiState(
    val date: LocalDate,
) {
    fun transformToDateString(context: Context, isLast: Boolean = false): String = when (isLast) {
        true -> context.getString(
            R.string.competitionfilter_duration_date_last_format,
            date.year % 1000,
            date.monthValue,
            date.dayOfMonth,
        )

        false -> context.getString(
            R.string.competitionfilter_duration_date_format,
            date.year % 1000,
            date.monthValue,
            date.dayOfMonth,
        )
    }

    companion object {
        fun from(selectedDate: LocalDate): CompetitionSelectedFilteringDateOptionUiState =
            CompetitionSelectedFilteringDateOptionUiState(
                date = selectedDate,
            )
    }
}
