package com.emmsale.data.eventdetail.dto

import com.emmsale.data.eventdetail.EventDetail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class EventDetailApiModel(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("status")
    val status: String,
    @SerialName("location")
    val location: String,
    @SerialName("startDate")
    val startDate: String,
    @SerialName("endDate")
    val endDate: String,
    @SerialName("informationUrl")
    val informationUrl: String,
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("imageUrl")
    val imageUrl: String?,
    @SerialName("remainingDays")
    val remainingDays: Int,
    @SerialName("type")
    val type: String,
) {
    fun toData(): EventDetail = EventDetail(
        id = id,
        name = name,
        status = status,
        location = location,
        startDate = startDate.toLocalDateTime(),
        endDate = endDate.toLocalDateTime(),
        informationUrl = informationUrl,
        tags = tags,
        imageUrl = imageUrl,
        remainingDays = remainingDays,
        type = type,
    )

    private fun String.toLocalDateTime(): LocalDateTime {
        val format = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")
        return LocalDateTime.parse(this, format)
    }
}
