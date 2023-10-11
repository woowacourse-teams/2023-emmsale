package com.emmsale.presentation.ui.blockMemberList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.BlockedMemberRepository
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.blockMemberList.uiState.BlockedMembersUiEvent
import com.emmsale.presentation.ui.blockMemberList.uiState.BlockedMembersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberBlockViewModel @Inject constructor(
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

            when (val result = blockedMemberRepository.getBlockedMembers()) {
                is Failure, NetworkError -> _blockedMembers.value = blockedMembers.value.copy(
                    isLoading = false,
                    isError = true,
                )

                is Success ->
                    _blockedMembers.value = BlockedMembersUiState.from(result.data)

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun unblockMember(blockId: Long) {
        viewModelScope.launch {
            when (val result = blockedMemberRepository.deleteBlockedMember(blockId)) {
                is Failure, NetworkError -> _event.value = BlockedMembersUiEvent.DELETE_ERROR
                is Success ->
                    _blockedMembers.value = blockedMembers.value.deleteBlockedMember(blockId)

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun resetEvent() {
        _event.value = null
    }
}
