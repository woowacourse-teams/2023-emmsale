package com.emmsale.data.education

import com.emmsale.data.education.dto.EducationApiModel

data class Education(
    val name: String,
) {
    companion object {
        fun from(educationApiModel: EducationApiModel): Education = Education(
            name = educationApiModel.name,
        )

        fun from(educationApiModels: List<EducationApiModel>): List<Education> =
            educationApiModels.map { from(it) }
    }
}
