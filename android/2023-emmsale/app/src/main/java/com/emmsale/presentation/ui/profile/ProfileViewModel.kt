package com.emmsale.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.messageRoom.MessageRoomRepository
import com.emmsale.data.repository.interfaces.ActivityRepository
import com.emmsale.data.repository.interfaces.BlockedMemberRepository
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.profile.uiState.BlockedMemberUiState
import com.emmsale.presentation.ui.profile.uiState.ProfileUiEvent
import com.emmsale.presentation.ui.profile.uiState.ProfileUiState
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val memberId: Long,
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val activityRepository: ActivityRepository,
    private val messageRoomRepository: MessageRoomRepository,
    private val blockedMemberRepository: BlockedMemberRepository,
) : ViewModel(), Refreshable {

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
        _profile.value = _profile.value.changeToLoadingState()
        viewModelScope.launch {
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
        }
    }

    fun isBlocked(): Boolean {
        return memberId in _blockedMembers.value.map { it.blockedMemberId }
    }

    fun blockMember() {
        viewModelScope.launch {
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
        }
    }

    fun unblockMember() {
        viewModelScope.launch {
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
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
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
        }
    }

    companion object {
        fun factory(memberId: Long) = ViewModelFactory {
            ProfileViewModel(
                memberId = memberId,
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                memberRepository = KerdyApplication.repositoryContainer.memberRepository,
                activityRepository = KerdyApplication.repositoryContainer.activityRepository,
                messageRoomRepository = KerdyApplication.repositoryContainer.messageRoomRepository,
                blockedMemberRepository = KerdyApplication.repositoryContainer.blockedMemberRepository,
            )
        }
    }
}
