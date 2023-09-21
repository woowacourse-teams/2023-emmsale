package com.emmsale.presentation.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.ActivityRepository
import com.emmsale.data.repository.interfaces.BlockedMemberRepository
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.profile.uiState.BlockedMemberUiState
import com.emmsale.presentation.ui.profile.uiState.ProfileEvent
import com.emmsale.presentation.ui.profile.uiState.ProfileUiState
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val memberId: Long,
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val activityRepository: ActivityRepository,
    private val blockedMemberRepository: BlockedMemberRepository,
) : ViewModel(), Refreshable {

    val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _profile = NotNullMutableLiveData(ProfileUiState.FIRST_LOADING)
    val profile: NotNullLiveData<ProfileUiState> = _profile

    private val _blockedMembers = NotNullMutableLiveData(listOf<BlockedMemberUiState>())

    private val _event = MutableLiveData<ProfileEvent?>(null)
    val event: LiveData<ProfileEvent?> = _event

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
                is Failure, NetworkError -> _event.value = ProfileEvent.BLOCK_FAIL
                is Success -> {
                    _event.value = ProfileEvent.BLOCK_COMPLETE
                    refresh()
                }

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun unblockMember() {
        viewModelScope.launch {
            val blockId = _blockedMembers.value.find { it.blockedMemberId == memberId }?.blockId
                ?: return@launch
            when (val result = blockedMemberRepository.deleteBlockedMember(blockId)) {
                is Failure, NetworkError -> _event.value = ProfileEvent.UNBLOCK_FAIL
                is Success -> {
                    _event.value = ProfileEvent.UNBLOCK_SUCCESS
                    refresh()
                }

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun removeEvent() {
        _event.value = null
    }

    fun sendMessage(message: String) {
    }

    companion object {
        fun factory(memberId: Long) = ViewModelFactory {
            ProfileViewModel(
                memberId = memberId,
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                memberRepository = KerdyApplication.repositoryContainer.memberRepository,
                activityRepository = KerdyApplication.repositoryContainer.activityRepository,
                blockedMemberRepository = KerdyApplication.repositoryContainer.blockedMemberRepository,
            )
        }
    }
}
