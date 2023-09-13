package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.RecruitmentResponse
import com.emmsale.data.model.Recruitment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun List<RecruitmentResponse>.toData(): List<Recruitment> = map {
    it.toData()
}

fun RecruitmentResponse.toData(): Recruitment = Recruitment(
    id = id,
    memberId = memberId,
    name = name,
    imageUrl = imageUrl,
    content = content,
    updatedDate = updatedAt.toLocalDate(),
)

private fun String.toLocalDate(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(this, formatter)
}
