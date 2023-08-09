package com.emmsale.presentation.ui.eventdetail.uistate

import com.emmsale.data.eventdetail.EventDetail
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
        val imageUrl: String?,
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
                    startDate = getGeneralDateFormat(startDate),
                    endDate = getGeneralDateFormat(endDate),
                    informationUrl = informationUrl,
                    tags = tags,
                    imageUrl = imageUrl,
                )
            }
        }

        private fun getGeneralDateFormat(dateString: String): String {
            val formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
            val dateTime = LocalDateTime.parse(dateString, formatter)

            val targetDate = LocalDate.of(dateTime.year, dateTime.month, dateTime.dayOfMonth)
            val targetTime = LocalTime.of(dateTime.hour, dateTime.minute)
            val targetDateTime = LocalDateTime.of(targetDate, targetTime)

            val resultFormatter = DateTimeFormatter.ofPattern("yyyy.M.d HH:mm")
            return targetDateTime.format(resultFormatter)
        }
    }
}
