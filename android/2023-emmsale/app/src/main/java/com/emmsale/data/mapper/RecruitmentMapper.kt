package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.RecruitmentResponse
import com.emmsale.data.model.RecruitmentPost
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun List<RecruitmentResponse>.toData(): List<RecruitmentPost> = map {
    it.toData()
}

fun RecruitmentResponse.toData(): RecruitmentPost = RecruitmentPost(
    id = id,
    memberId = memberId,
    name = name,
    imageUrl = imageUrl,
    content = content,
    updatedDate = updatedAt.toLocalDateTime(),
)

private fun String.toLocalDateTime(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(this, formatter)
}
