package com.emmsale.presentation.ui.scrappedEventList.uiState

import com.emmsale.data.model.ScrappedEvent

data class ScrappedEventUiState(
    val data: ScrappedEvent,
) {

    fun getStatusString(): String = when (data.status) {
        ScrappedEvent.Status.IN_PROGRESS -> "진행중"
        ScrappedEvent.Status.UPCOMING -> "D-${data.dDay}"
        ScrappedEvent.Status.ENDED -> "마감"
    }

    companion object {
        fun from(scrappedEvent: ScrappedEvent) = ScrappedEventUiState(
            data = scrappedEvent,
        )
    }
}
