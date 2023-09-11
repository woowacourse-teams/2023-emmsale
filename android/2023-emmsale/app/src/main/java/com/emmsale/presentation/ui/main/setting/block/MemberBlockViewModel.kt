package com.emmsale.presentation.ui.main.setting.block

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.blockedMember.BlockedMemberRepository
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.main.setting.block.uistate.BlockedMembersUiEvent
import com.emmsale.presentation.ui.main.setting.block.uistate.BlockedMembersUiState
import kotlinx.coroutines.launch

class MemberBlockViewModel(
    private val blockedMemberRepository: BlockedMemberRepository,
) : ViewModel(), Refreshable {
    private val _blockedMembers = NotNullMutableLiveData(BlockedMembersUiState())
    val blockedMembers: NotNullLiveData<BlockedMembersUiState> = _blockedMembers

    private val _event = MutableLiveData<BlockedMembersUiEvent?>(null)
    val event: LiveData<BlockedMembersUiEvent?> = _event

    init {
        refresh()
    }

    override fun refresh() {
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
                    isError = true,
                )
            }
        }
    }

    fun unblockMember(blockId: Long) {
        viewModelScope.launch {
            when (blockedMemberRepository.deleteBlockedMember(blockId)) {
                is ApiSuccess ->
                    _blockedMembers.value = blockedMembers.value.deleteBlockedMember(blockId)

                is ApiError, is ApiException -> _event.value = BlockedMembersUiEvent.DELETE_ERROR
            }
        }
    }

    fun resetEvent() {
        _event.value = null
    }

    companion object {
        val factory = ViewModelFactory {
            MemberBlockViewModel(
                blockedMemberRepository = KerdyApplication.repositoryContainer.blockedMemberRepository,
            )
        }
    }
}
