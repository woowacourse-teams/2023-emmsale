package com.emmsale.data.eventTag

import com.emmsale.data.eventTag.dto.ConferenceTagApiModel
import retrofit2.Response

class FakeEventTagService : EventTagService {
    override suspend fun getConferenceTags(
        category: String,
    ): Response<List<ConferenceTagApiModel>> = Response.success(provideConferenceTags())

    private fun provideConferenceTags(): List<ConferenceTagApiModel> = listOf(
        ConferenceTagApiModel(
            id = 1,
            name = "백엔드"
        ),
        ConferenceTagApiModel(
            id = 2,
            name = "프론트엔드"
        ),
        ConferenceTagApiModel(
            id = 3,
            name = "안드로이드"
        ),
        ConferenceTagApiModel(
            id = 4,
            name = "IOS"
        ),
        ConferenceTagApiModel(
            id = 5,
            name = "AI"
        ),
        ConferenceTagApiModel(
            id = 6,
            name = "알고리즘"
        ),
        ConferenceTagApiModel(
            id = 7,
            name = "정보보안"
        ),
    )
}
