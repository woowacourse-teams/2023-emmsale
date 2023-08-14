package com.emmsale.data.recruitment.mapper

import com.emmsale.data.recruitment.Recruitment
import com.emmsale.data.recruitment.dto.RecruitmentApiModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun List<RecruitmentApiModel>.toData(): List<Recruitment> = map {
    it.toData()
}

fun RecruitmentApiModel.toData(): Recruitment = Recruitment(
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
