package com.emmsale.data.event.dto

import com.emmsale.data.event.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class EventApiModel(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("startDate")
    val startDate: String,
    @SerialName("endDate")
    val endDate: String,
    @SerialName("status")
    val status: String,
    @SerialName("tags")
    val tags: List<String>
) {
    fun toData(): Event = Event(
        id = id,
        name = name,
        startDate = startDate,
        endDate = endDate,
        status = status,
        tags = tags
    )
}

fun List<EventApiModel>.toData(): List<Event> = map { it.toData() }
