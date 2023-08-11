package com.emmsale.data.conference

import com.emmsale.data.common.ApiResult
import com.emmsale.data.conferenceStatus.ConferenceStatus
import com.emmsale.data.eventTag.EventTag
import java.time.LocalDate

interface EventRepository {
    suspend fun getEvents(
        category: EventCategory,
        statuses: List<ConferenceStatus> = emptyList(),
        tags: List<EventTag> = emptyList(),
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ): ApiResult<List<Conference>>
}
