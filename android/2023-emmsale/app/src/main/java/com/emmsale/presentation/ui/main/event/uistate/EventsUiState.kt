package com.emmsale.presentation.ui.main.event.uistate

import com.emmsale.data.event.Event
import com.emmsale.data.event.EventStatus

sealed class EventsUiState {
    data class Success(val events: List<EventUiState>) : EventsUiState() {
        val eventSize: Int = events.size
    }

    object Loading : EventsUiState()
    object Error : EventsUiState()
}

data class EventUiState(
    val id: Long,
    val name: String,
    val tags: List<String>,
    val status: String,
    val posterUrl: String?,
) {
    companion object {
        fun from(event: Event): EventUiState {
            return EventUiState(
                id = event.id,
                name = event.name,
                tags = event.tags,
                status = getStatus(event),
                posterUrl = event.posterUrl,
            )
        }

        private fun getStatus(event: Event): String = when (event.status) {
            EventStatus.IN_PROGRESS -> "진행중"
            EventStatus.SCHEDULED -> "D-${event.dDay}"
            EventStatus.ENDED -> "마감"
        }
    }
}
