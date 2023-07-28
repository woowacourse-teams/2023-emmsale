package com.emmsale.presentation.ui.main.event.uistate

import com.emmsale.data.event.Event
import java.time.LocalDateTime

sealed class EventsUiSTate {
    data class Success(val events: List<EventUiState>) : EventsUiSTate()
    object Loading : EventsUiSTate()
    object Error : EventsUiSTate()
}

data class EventUiState(
    val id: Long,
    val name: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val tags: List<String>,
    val status: String,
) {
    companion object {
        fun from(event: Event): EventUiState = EventUiState(
            id = event.id,
            name = event.name,
            startDate = event.startDate,
            endDate = event.endDate,
            tags = event.tags,
            status = event.status,
        )
    }
}
