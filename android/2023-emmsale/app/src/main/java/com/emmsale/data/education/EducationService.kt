package com.emmsale.data.education

import com.emmsale.data.education.dto.EducationApiModel
import retrofit2.Response

interface EducationService {
    fun getEducations(): Response<List<EducationApiModel>>
}

internal class FakeEducationService : EducationService {
    override fun getEducations(): Response<List<EducationApiModel>> = Response.success(
        listOf(
            EducationApiModel("우아한테크코스"),
            EducationApiModel("네이버 부트캠프"),
            EducationApiModel("카카오 부트캠프"),
            EducationApiModel("패스트 캠퍼스"),
        )
    )
}
