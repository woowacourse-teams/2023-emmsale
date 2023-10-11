package com.emmsale.presentation.ui.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.ActivityRepository
import com.emmsale.data.repository.interfaces.BlockedMemberRepository
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.MessageRoomRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.profile.uiState.BlockedMemberUiState
import com.emmsale.presentation.ui.profile.uiState.ProfileUiEvent
import com.emmsale.presentation.ui.profile.uiState.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val activityRepository: ActivityRepository,
    private val messageRoomRepository: MessageRoomRepository,
    private val blockedMemberRepository: BlockedMemberRepository,
) : ViewModel(), Refreshable {
    private val memberId: Long = requireNotNull(savedStateHandle[KEY_MEMBER_ID]) {
        "[ERROR] 멤버 아이디를 가져오지 못했어요."
    }
    val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _profile = NotNullMutableLiveData(ProfileUiState.FIRST_LOADING)
    val profile: NotNullLiveData<ProfileUiState> = _profile

    private val _blockedMembers = NotNullMutableLiveData(listOf<BlockedMemberUiState>())

    private val _uiEvent: NotNullMutableLiveData<Event<ProfileUiEvent>> =
        NotNullMutableLiveData(Event(ProfileUiEvent.None))
    val uiEvent: NotNullLiveData<Event<ProfileUiEvent>> = _uiEvent

    init {
        refresh()
    }

    override fun refresh() {
        viewModelScope.launch {
            _profile.value = _profile.value.changeToLoadingState()
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }
            launch {
                when (val result = memberRepository.getMember(memberId)) {
                    is Failure, NetworkError ->
                        _profile.value = _profile.value.changeToFetchingErrorState()

                    is Success ->
                        _profile.value = _profile.value.changeMemberState(result.data, token.uid)

                    is Unexpected -> throw Throwable(result.error)
                }
            }
            launch {
                when (val result = activityRepository.getActivities(memberId)) {
                    is Failure, NetworkError ->
                        _profile.value = _profile.value.changeToFetchingErrorState()

                    is Success ->
                        _profile.value = _profile.value.changeActivityState(result.data)

                    is Unexpected -> throw Throwable(result.error)
                }
            }
            launch {
                when (val result = blockedMemberRepository.getBlockedMembers()) {
                    is Failure, NetworkError ->
                        _profile.value = _profile.value.changeToFetchingErrorState()

                    is Success ->
                        _blockedMembers.value = result.data.map { BlockedMemberUiState.from(it) }

                    is Unexpected -> throw Throwable(result.error)
                }
            }
            _profile.value = _profile.value.copy(isLoading = false)
        }
    }

    fun isBlocked(): Boolean {
        return memberId in _blockedMembers.value.map { it.blockedMemberId }
    }

    fun blockMember() {
        viewModelScope.launch {
            _profile.value = _profile.value.copy(isLoading = true)
            when (val result = memberRepository.blockMember(memberId)) {
                is Failure -> _uiEvent.value = Event(ProfileUiEvent.BlockFail)

                NetworkError -> _profile.value = _profile.value.copy(isError = true)
                is Success -> {
                    _uiEvent.value = Event(ProfileUiEvent.BlockComplete)
                    refresh()
                }

                is Unexpected ->
                    _uiEvent.value = Event(ProfileUiEvent.UnexpectedError(result.error.toString()))
            }
            _profile.value = _profile.value.copy(isLoading = false)
        }
    }

    fun unblockMember() {
        viewModelScope.launch {
            _profile.value = _profile.value.copy(isLoading = true)
            val blockId = _blockedMembers.value.find { it.blockedMemberId == memberId }?.blockId
                ?: return@launch
            when (val result = blockedMemberRepository.deleteBlockedMember(blockId)) {
                is Failure -> _uiEvent.value = Event(ProfileUiEvent.UnblockFail)
                NetworkError -> _profile.value = _profile.value.copy(isError = true)
                is Success -> {
                    _uiEvent.value = Event(ProfileUiEvent.UnblockSuccess)
                    refresh()
                }

                is Unexpected ->
                    _uiEvent.value = Event(ProfileUiEvent.UnexpectedError(result.error.toString()))
            }
            _profile.value = _profile.value.copy(isLoading = false)
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            _profile.value = _profile.value.copy(isLoading = true)
            when (
                val result =
                    messageRoomRepository.sendMessage(uid, _profile.value.memberId, message)
            ) {
                is Failure ->
                    _uiEvent.value = Event(ProfileUiEvent.MessageSendFail)

                NetworkError -> _profile.value = _profile.value.copy(isError = true)
                is Success ->
                    _uiEvent.value = Event(
                        ProfileUiEvent.MessageSendComplete(result.data, _profile.value.memberId),
                    )

                is Unexpected ->
                    _uiEvent.value = Event(ProfileUiEvent.UnexpectedError(result.error.toString()))
            }
            _profile.value = _profile.value.copy(isLoading = false)
        }
    }

    companion object {
        const val KEY_MEMBER_ID = "KEY_MEMBER_ID"
    }
}
