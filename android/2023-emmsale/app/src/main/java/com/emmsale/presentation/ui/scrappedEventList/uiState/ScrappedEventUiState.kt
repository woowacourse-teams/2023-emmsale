package com.emmsale.presentation.ui.scrappedEventList.uiState

import com.emmsale.data.model.ConferenceStatus
import com.emmsale.data.model.ScrappedEvent

data class ScrappedEventUiState(
    val scrapId: Long = DEFAULT_ID,
    val eventId: Long = DEFAULT_ID,
    val name: String = "",
    val status: String = "",
    val imageUrl: String?,
    val tags: List<String> = listOf(),
) {
    companion object {
        private const val DEFAULT_ID = -1L

        fun from(scrappedEvent: ScrappedEvent) = ScrappedEventUiState(
            scrappedEvent.scrapId,
            scrappedEvent.eventId,
            scrappedEvent.name,
            getStatus(scrappedEvent),
            scrappedEvent.imageUrl,
            scrappedEvent.tags,
        )

        private fun getStatus(scrappedEvent: ScrappedEvent): String = when (scrappedEvent.status) {
            ConferenceStatus.IN_PROGRESS -> "진행중"
            ConferenceStatus.SCHEDULED -> "진행 예정"
            ConferenceStatus.ENDED -> "마감"
        }
    }
}
