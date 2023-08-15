package com.emmsale.data.eventTag.local

import com.emmsale.data.eventTag.remote.dto.EventTagApiModel
import retrofit2.Response

class EventTagLocalDataSource {
    fun getEventTags(
        eventCategory: String,
    ): Response<List<EventTagApiModel>> = when (eventCategory) {
        CONFERENCE -> Response.success(provideConferenceTags())
        COMPETITION -> Response.success(provideCompetitionTags())
        else -> throw IllegalArgumentException(INVALID_EVENT_CATEGORY_ERROR_MESSAGE)
    }

    fun getEventTagByIds(
        eventCategory: String,
        ids: Array<Long>,
    ): Response<List<EventTagApiModel>> = when (eventCategory) {
        CONFERENCE -> Response.success(provideConferenceTags().filter { ids.contains(it.id) })
        COMPETITION -> Response.success(provideCompetitionTags().filter { ids.contains(it.id) })
        else -> throw IllegalArgumentException(INVALID_EVENT_CATEGORY_ERROR_MESSAGE)
    }

    private fun provideConferenceTags(): List<EventTagApiModel> = listOf(
        EventTagApiModel(
            id = 1,
            name = "백엔드",
        ),
        EventTagApiModel(
            id = 2,
            name = "프론트엔드",
        ),
        EventTagApiModel(
            id = 3,
            name = "안드로이드",
        ),
        EventTagApiModel(
            id = 4,
            name = "IOS",
        ),
        EventTagApiModel(
            id = 5,
            name = "AI",
        ),
        EventTagApiModel(
            id = 6,
            name = "알고리즘",
        ),
        EventTagApiModel(
            id = 7,
            name = "정보보안",
        ),
    )

    private fun provideCompetitionTags(): List<EventTagApiModel> = listOf(
        EventTagApiModel(
            id = 1,
            name = "백엔드",
        ),
        EventTagApiModel(
            id = 2,
            name = "프론트엔드",
        ),
        EventTagApiModel(
            id = 3,
            name = "안드로이드",
        ),
        EventTagApiModel(
            id = 4,
            name = "IOS",
        ),
        EventTagApiModel(
            id = 5,
            name = "AI",
        ),
        EventTagApiModel(
            id = 6,
            name = "알고리즘",
        ),
        EventTagApiModel(
            id = 7,
            name = "정보보안",
        ),
    )

    companion object {
        private const val CONFERENCE = "CONFERENCE"
        private const val COMPETITION = "COMPETITION"
        private const val INVALID_EVENT_CATEGORY_ERROR_MESSAGE = "올바르지 않은 행사 카테고리입니다."
    }
}