package com.emmsale.data.recruitment.mapper

import com.emmsale.data.recruitment.Recruitment
import com.emmsale.data.recruitment.dto.RecruitmentApiModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun List<RecruitmentApiModel>.toData(): List<Recruitment> = map {
    Recruitment(
        id = it.id,
        memberId = it.memberId,
        name = it.name,
        imageUrl = it.imageUrl,
        description = it.description,
        content = it.content,
        updatedDate = it.updatedAt.toLocalDate(),
    )
}

private fun String.toLocalDate(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    return LocalDate.parse(this, formatter)
}
