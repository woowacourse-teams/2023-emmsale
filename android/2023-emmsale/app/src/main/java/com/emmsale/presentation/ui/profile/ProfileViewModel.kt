package com.emmsale.presentation.ui.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.model.BlockedMember
import com.emmsale.data.model.Member
import com.emmsale.data.repository.interfaces.BlockedMemberRepository
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.MessageRoomRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.base.NetworkViewModel
import com.emmsale.presentation.common.ScreenUiState
import com.emmsale.presentation.common.UiEvent
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.profile.uiState.ProfileUiEvent
import com.emmsale.presentation.ui.profile.uiState.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val messageRoomRepository: MessageRoomRepository,
    private val blockedMemberRepository: BlockedMemberRepository,
) : NetworkViewModel() {
    private val memberId: Long = requireNotNull(savedStateHandle[KEY_MEMBER_ID]) {
        "[ERROR] 멤버 아이디를 가져오지 못했어요."
    }
    private val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _profile = NotNullMutableLiveData(ProfileUiState(false, Member()))
    val profile: NotNullLiveData<ProfileUiState> = _profile

    private val _blockedMembers = NotNullMutableLiveData(listOf<BlockedMember>())

    private val _canSendMessage = NotNullMutableLiveData(true)
    val canSendMessage: NotNullLiveData<Boolean> = _canSendMessage

    private val _uiEvent: NotNullMutableLiveData<UiEvent<ProfileUiEvent>> =
        NotNullMutableLiveData(UiEvent(ProfileUiEvent.None))
    val uiEvent: NotNullLiveData<UiEvent<ProfileUiEvent>> = _uiEvent

    init {
        fetchAll()
    }

    private fun fetchAll(): Job = viewModelScope.launch {
        changeToLoadingState()
        refresh()
    }

    override fun refresh(): Job = viewModelScope.launch {
        listOf(fetchProfile(), fetchBlockedMembers()).joinAll()
        if (_screenUiState.value == ScreenUiState.NETWORK_ERROR) return@launch
        _screenUiState.value = ScreenUiState.NONE
    }

    private fun fetchProfile(): Job = fetchData(
        fetchData = { memberRepository.getMember(memberId) },
        onSuccess = { _profile.value = ProfileUiState(it, uid) },
        onLoading = {},
    )

    private fun fetchBlockedMembers(): Job = fetchData(
        fetchData = { blockedMemberRepository.getBlockedMembers() },
        onSuccess = { _blockedMembers.value = it },
        onLoading = {},
        onNetworkError = {},
    )

    fun isBlocked(): Boolean = memberId in _blockedMembers.value.map { it.blockedMemberId }

    fun blockMember(): Job = commandAndRefresh(
        command = { memberRepository.blockMember(memberId) },
        onSuccess = { _uiEvent.value = UiEvent(ProfileUiEvent.BlockComplete) },
        onFailure = { _, _ -> _uiEvent.value = UiEvent(ProfileUiEvent.BlockFail) },
    )

    fun unblockMember(): Job = commandAndRefresh(
        command = {
            val blockId = _blockedMembers.value.find { it.blockedMemberId == memberId }?.blockId
                ?: return@commandAndRefresh Failure(-1, null)
            blockedMemberRepository.deleteBlockedMember(blockId)
        },
        onSuccess = { _uiEvent.value = UiEvent(ProfileUiEvent.UnblockSuccess) },
        onFailure = { _, _ -> _uiEvent.value = UiEvent(ProfileUiEvent.UnblockFail) },
    )

    fun sendMessage(message: String): Job = command(
        command = { messageRoomRepository.sendMessage(uid, _profile.value.member.id, message) },
        onSuccess = {
            _uiEvent.value =
                UiEvent(ProfileUiEvent.MessageSendComplete(it, _profile.value.member.id))
        },
        onFailure = { _, _ -> _uiEvent.value = UiEvent(ProfileUiEvent.MessageSendFail) },
        onStart = { _canSendMessage.value = false },
        onFinish = { _canSendMessage.value = true },
    )

    companion object {
        const val KEY_MEMBER_ID = "KEY_MEMBER_ID"
    }
}
