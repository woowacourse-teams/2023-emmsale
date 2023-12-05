package com.emmsale.presentation.ui.recruitmentDetail.uiState

import com.emmsale.data.model.Recruitment

data class RecruitmentUiState(
    val recruitment: Recruitment = Recruitment(),
    val isMyPost: Boolean = false,
) {
    constructor(recruitment: Recruitment, uid: Long) : this(
        recruitment = recruitment,
        isMyPost = uid == recruitment.writer.id,
    )
}
