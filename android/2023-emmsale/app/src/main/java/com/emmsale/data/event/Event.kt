package com.emmsale.data.event

import com.emmsale.data.event.dto.EventApiModel

data class Event(
    val id: Int,
    val name: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val tags: List<String>
) {
    companion object {
        fun from(eventApiModel: EventApiModel): Event = Event(
            id = eventApiModel.id,
            name = eventApiModel.name,
            startDate = eventApiModel.startDate,
            endDate = eventApiModel.endDate,
            status = eventApiModel.status,
            tags = eventApiModel.tags
        )
    }
}
