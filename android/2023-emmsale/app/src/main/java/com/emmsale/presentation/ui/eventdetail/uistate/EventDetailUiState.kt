package com.emmsale.presentation.ui.eventdetail.uistate

import com.emmsale.data.eventdetail.EventDetail
import com.emmsale.presentation.utils.DateStringFormat

sealed class EventDetailUiState {
    data class Success(
        val id: Long,
        val name: String,
        val status: String,
        val location: String,
        val startDate: String,
        val endDate: String,
        val informationUrl: String,
        val tags: List<String>,
        val imageUrl: String,
    ) : EventDetailUiState()

    object Error : EventDetailUiState()

    companion object {
        fun from(eventDetail: EventDetail): Success {
            return with(eventDetail) {
                Success(
                    id = id,
                    name = name,
                    status = status,
                    location = location,
                    startDate = DateStringFormat.getGeneralDateFormat(startDate),
                    endDate = DateStringFormat.getGeneralDateFormat(endDate),
                    informationUrl = informationUrl,
                    tags = tags,
                    imageUrl = imageUrl,
                )
            }
        }
    }
}
