package com.emmsale.data.eventTag

import com.emmsale.data.eventTag.dto.EventTagApiModel
import retrofit2.Response

class FakeEventTagService : EventTagService {
    override suspend fun getEventTags(
        eventCategory: String,
    ): Response<List<EventTagApiModel>> = Response.success(provideConferenceTags())

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
}
