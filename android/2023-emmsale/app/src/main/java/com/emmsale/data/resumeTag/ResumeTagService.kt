package com.emmsale.data.resumeTag

import com.emmsale.data.resumeTag.dto.ResumeTagApiModel
import retrofit2.Response

interface ResumeTagService {
    suspend fun getEducationTags(): Response<List<ResumeTagApiModel>>
    suspend fun getConferenceTags(): Response<List<ResumeTagApiModel>>
}

internal class FakeResumeTagService : ResumeTagService {
    override suspend fun getEducationTags(): Response<List<ResumeTagApiModel>> = Response.success(
        listOf(
            ResumeTagApiModel("우아한테크코스"),
            ResumeTagApiModel("네이버 부트캠프"),
            ResumeTagApiModel("카카오 부트캠프"),
            ResumeTagApiModel("패스트 캠퍼스"),
        )
    )

    override suspend fun getConferenceTags(): Response<List<ResumeTagApiModel>> = Response.success(
        listOf(
            ResumeTagApiModel("인프콘 2023"),
            ResumeTagApiModel("네이버 클라우드 플랫폼과 함께 살펴보는 Cloud Trends"),
            ResumeTagApiModel("CES 2022 리뷰 & 인사이트 콘서트"),
            ResumeTagApiModel("AWS Builders 온라인 컨퍼런스"),
            ResumeTagApiModel(".NET Conf 2022 x Seoul"),
            ResumeTagApiModel("AWS Innovate AI/ML 온라인 컨퍼런스"),
            ResumeTagApiModel("NDC22"),
        )
    )
}
