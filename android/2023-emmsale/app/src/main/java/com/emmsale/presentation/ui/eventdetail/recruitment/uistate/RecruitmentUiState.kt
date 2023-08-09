package com.emmsale.presentation.ui.eventdetail.recruitment.uistate

import com.emmsale.data.recruitment.Recruitment

data class RecruitmentUiState(
    val id: Long,
    val memberId: Long,
    val name: String,
    val imageUrl: String,
    val description: String?,
) {
    companion object {
        fun from(recruitment: Recruitment): RecruitmentUiState = RecruitmentUiState(
            id = recruitment.id,
            memberId = recruitment.memberId,
            name = recruitment.name,
            imageUrl = recruitment.imageUrl,
            description = recruitment.description,
        )
    }
}
