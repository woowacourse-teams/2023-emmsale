package com.emmsale.presentation.ui.main.event.conferenceFilter.uistate

import android.content.Context
import android.os.Parcelable
import com.emmsale.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConferenceFilterDateUiState(
    var year: Int,
    var month: Int,
    var day: Int,
) : Parcelable {
    fun transformToDateString(context: Context, isLast: Boolean = false): String = when (isLast) {
        true -> context.getString(
            R.string.eventfilter_duration_date_last_format,
            year % 1000,
            month,
            day,
        )

        false -> context.getString(
            R.string.eventfilter_duration_date_format,
            year % 1000,
            month,
            day,
        )
    }
}
