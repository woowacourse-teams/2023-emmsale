package com.emmsale.presentation.ui.eventdetail.uistate

import com.emmsale.data.eventdetail.EventDetail
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class EventDetailUiState(
    val id: Long = DEFAULT_ID,
    val name: String = "",
    val status: String = "",
    val location: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val informationUrl: String = "",
    val tags: List<String> = listOf(),
    val imageUrl: String = "",
    val isError: Boolean = false,
    val isLoading: Boolean = false,
) {

    companion object {
        private const val DEFAULT_ID = -1L
        fun from(eventDetail: EventDetail): EventDetailUiState {
            return with(eventDetail) {
                EventDetailUiState(
                    id = id,
                    name = name,
                    status = status,
                    location = location,
                    startDate = getGeneralDateFormat(startDate),
                    endDate = getGeneralDateFormat(endDate),
                    informationUrl = informationUrl,
                    tags = tags,
                    imageUrl = postImageUrl ?: "",
                    isError = false,
                    isLoading = false,
                )
            }
        }

        private fun getGeneralDateFormat(dateTime: LocalDateTime): String {
            val resultFormatter = DateTimeFormatter.ofPattern("yyyy.M.d HH:mm")
            return dateTime.format(resultFormatter)
        }
    }
}
