package com.emmsale.data.event.dto

import com.emmsale.data.event.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class ConferenceApiModel(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("startDate")
    val startDate: String, // format : "2023:09:03:12:00:00",
    @SerialName("endDate")
    val endDate: String, // format : "2023:09:03:12:00:00",
    @SerialName("status")
    val status: String,
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("imageUrl")
    val posterUrl: String,
    @SerialName("remainingDays")
    val dDay: Int,
) {
    fun toData(): Event = Event(
        id = id,
        name = name,
        startDate = parseDate(startDate),
        endDate = parseDate(endDate),
        status = status,
        tags = tags,
        posterUrl = posterUrl,
        dDay = dDay,
    )

    private fun parseDate(date: String): LocalDateTime {
        val dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
        return LocalDateTime.parse(date, dateTimeFormatter)
    }

    companion object {
        private const val DATE_TIME_FORMAT = "yyyy:MM:dd:HH:mm:ss"
    }
}

fun List<ConferenceApiModel>.toData(): List<Event> = map { it.toData() }
