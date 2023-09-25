package com.emmsale.presentation.ui.scrappedEventList.uiState

import com.emmsale.data.model.ScrappedEvent
import com.emmsale.data.model.Status

data class ScrappedEventUiState(
    val data: ScrappedEvent,
) {

    fun getStatusString(): String = when (data.status) {
        Status.IN_PROGRESS -> "진행중"
        Status.UPCOMING -> "D-${data.dDay}"
        Status.ENDED -> "마감"
    }

    companion object {
        fun from(scrappedEvent: ScrappedEvent) = ScrappedEventUiState(
            data = scrappedEvent,
        )
    }
}
