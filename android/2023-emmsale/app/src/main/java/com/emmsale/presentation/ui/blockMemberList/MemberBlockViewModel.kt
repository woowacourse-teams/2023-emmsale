package com.emmsale.presentation.ui.blockMemberList

import androidx.lifecycle.LiveData
import com.emmsale.data.model.BlockedMember
import com.emmsale.data.repository.interfaces.BlockedMemberRepository
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import com.emmsale.presentation.ui.blockMemberList.uiState.BlockedMembersUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class MemberBlockViewModel @Inject constructor(
    private val blockedMemberRepository: BlockedMemberRepository,
) : RefreshableViewModel() {
    private val _blockedMembers = NotNullMutableLiveData(listOf<BlockedMember>())
    val blockedMembers: NotNullLiveData<List<BlockedMember>> = _blockedMembers

    private val _uiEvent = SingleLiveEvent<BlockedMembersUiEvent>()
    val uiEvent: LiveData<BlockedMembersUiEvent> = _uiEvent

    init {
        fetchBlockedMembers()
    }

    private fun fetchBlockedMembers(): Job = fetchData(
        fetchData = { blockedMemberRepository.getBlockedMembers() },
        onSuccess = { _blockedMembers.value = it },
        onFailure = { _, _ -> _uiEvent.value = BlockedMembersUiEvent.FetchFail },
    )

    override fun refresh(): Job = refreshData(
        refresh = { blockedMemberRepository.getBlockedMembers() },
        onSuccess = { _blockedMembers.value = it },
    )

    fun unblockMember(blockId: Long): Job = command(
        command = { blockedMemberRepository.deleteBlockedMember(blockId) },
        onSuccess = {
            _blockedMembers.value = _blockedMembers.value.filter { it.blockId != blockId }
        },
        onFailure = { _, _ -> _uiEvent.value = BlockedMembersUiEvent.DeleteFail },
    )
}
