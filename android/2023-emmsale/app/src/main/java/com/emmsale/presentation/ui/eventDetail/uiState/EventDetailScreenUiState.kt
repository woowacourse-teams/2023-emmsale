package com.emmsale.presentation.ui.eventDetail.uiState

enum class EventDetailScreenUiState(private val position: Int) {
    INFORMATION(0),
    POST(1),
    RECRUITMENT(2),
    ;

    companion object {
        fun from(position: Int): EventDetailScreenUiState {
            return EventDetailScreenUiState.values().find { it.position == position } ?: INFORMATION
        }
    }
}
