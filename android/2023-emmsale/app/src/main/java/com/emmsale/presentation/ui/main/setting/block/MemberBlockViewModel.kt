package com.emmsale.presentation.ui.main.setting.block

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.blockedMember.BlockedMemberRepository
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.main.setting.block.uistate.BlockedMembersUiState
import kotlinx.coroutines.launch

class MemberBlockViewModel(
    private val blockedMemberRepository: BlockedMemberRepository,
) : ViewModel() {
    private val _blockedMembers = NotNullMutableLiveData(BlockedMembersUiState())
    val blockedMembers: NotNullLiveData<BlockedMembersUiState> = _blockedMembers

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
                    isFetchingError = true,
                )
            }
        }
    }

    fun unblockMember(blockId: Long) {
        viewModelScope.launch {
            when (blockedMemberRepository.deleteBlockedMember(blockId)) {
                is ApiSuccess ->
                    _blockedMembers.value = blockedMembers.value.deleteBlockedMember(blockId)

                is ApiError, is ApiException -> _blockedMembers.value = blockedMembers.value.copy(
                    isLoading = false,
                    isDeletingBlockedMemberError = true,
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
