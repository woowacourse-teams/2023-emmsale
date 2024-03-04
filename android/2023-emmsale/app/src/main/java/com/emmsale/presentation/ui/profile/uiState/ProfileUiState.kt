package com.emmsale.presentation.ui.profile.uiState

import com.emmsale.model.Member

data class ProfileUiState(
    val isLoginMember: Boolean,
    val member: Member = Member(),
) {
    constructor(member: Member, uid: Long) : this(uid == member.id, member)
}
