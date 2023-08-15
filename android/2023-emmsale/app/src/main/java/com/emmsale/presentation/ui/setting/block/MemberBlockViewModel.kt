package com.emmsale.presentation.ui.setting.block

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.blockedMember.BlockedMember
import com.emmsale.data.blockedMember.BlockedMemberRepository
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import kotlinx.coroutines.launch

data class BlockedMembersUiState(
    val blockedMembers: List<BlockedMemberUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isFetchingError: Boolean = false,
) {
    companion object {
        fun from(blockedMembers: List<BlockedMember>): BlockedMembersUiState =
            BlockedMembersUiState(blockedMembers = blockedMembers.map(BlockedMemberUiModel::from))
    }
}

data class BlockedMemberUiModel(
    val id: Long,
    val memberName: String,
    val profileImageUrl: String,
) {
    companion object {
        fun from(blockedMember: BlockedMember): BlockedMemberUiModel = BlockedMemberUiModel(
            id = blockedMember.id,
            memberName = blockedMember.memberName,
            profileImageUrl = blockedMember.profileImageUrl,
        )
    }
}

class MemberBlockViewModel(
    private val blockedMemberRepository: BlockedMemberRepository,
) : ViewModel() {
    private val _blockedMembers = NotNullMutableLiveData(BlockedMembersUiState())
    private val blockedMembers: NotNullLiveData<BlockedMembersUiState> = _blockedMembers

    init {
        fetchBlockedMembers()
    }

    private fun fetchBlockedMembers() {
        viewModelScope.launch {
            _blockedMembers.value = _blockedMembers.value.copy(isLoading = true)
            when (val blockedMembersResult = blockedMemberRepository.getBlockedMembers()) {
                is ApiSuccess ->
                    _blockedMembers.value = BlockedMembersUiState.from(blockedMembersResult.data)

                is ApiError, is ApiException -> _blockedMembers.value = blockedMembers.value.copy(
                    isLoading = false,
                    isFetchingError = true
                )
            }
        }
    }

    companion object {
        val factory = ViewModelFactory {
            MemberBlockViewModel(
                blockedMemberRepository = KerdyApplication.repositoryContainer.blockedMemberRepository,
            )
        }
    }
}