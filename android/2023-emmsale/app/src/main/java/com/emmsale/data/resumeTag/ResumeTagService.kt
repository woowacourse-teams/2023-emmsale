package com.emmsale.data.resumeTag

import com.emmsale.data.resumeTag.dto.ResumeTagApiModel
import retrofit2.Response

interface EducationTagService {
    fun getEducationTags(): Response<List<ResumeTagApiModel>>
}

internal class FakeEducationTagService : EducationTagService {
    override fun getEducationTags(): Response<List<ResumeTagApiModel>> = Response.success(
        listOf(
            ResumeTagApiModel("우아한테크코스"),
            ResumeTagApiModel("네이버 부트캠프"),
            ResumeTagApiModel("카카오 부트캠프"),
            ResumeTagApiModel("패스트 캠퍼스"),
        )
    )
}
