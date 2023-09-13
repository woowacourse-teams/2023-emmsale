package com.emmsale.presentation.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.repository.ActivityRepository
import com.emmsale.data.repository.BlockedMemberRepository
import com.emmsale.data.repository.MemberRepository
import com.emmsale.data.repository.TokenRepository
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
                    is ApiError, is ApiException ->
                        _profile.value = _profile.value.changeToFetchingErrorState()

                    is ApiSuccess ->
                        _profile.value = _profile.value.changeMemberState(result.data, token.uid)
                }
            }
            launch {
                when (val result = activityRepository.getActivities(memberId)) {
                    is ApiError, is ApiException ->
                        _profile.value = _profile.value.changeToFetchingErrorState()

                    is ApiSuccess ->
                        _profile.value = _profile.value.changeActivityState(result.data)
                }
            }
            launch {
                when (val result = blockedMemberRepository.getBlockedMembers()) {
                    is ApiError, is ApiException ->
                        _profile.value = _profile.value.changeToFetchingErrorState()

                    is ApiSuccess ->
                        _blockedMembers.value = result.data.map { BlockedMemberUiState.from(it) }
                }
            }
        }
    }

    fun isBlocked(): Boolean {
        return memberId in _blockedMembers.value.map { it.blockedMemberId }
    }

    fun blockMember() {
        viewModelScope.launch {
            when (memberRepository.blockMember(memberId)) {
                is ApiError, is ApiException -> _event.value = ProfileEvent.BLOCK_FAIL
                is ApiSuccess -> {
                    _event.value = ProfileEvent.BLOCK_COMPLETE
                    refresh()
                }
            }
        }
    }

    fun unblockMember() {
        viewModelScope.launch {
            val blockId = _blockedMembers.value.find { it.blockedMemberId == memberId }?.blockId
                ?: return@launch
            when (blockedMemberRepository.deleteBlockedMember(blockId)) {
                is ApiError, is ApiException -> _event.value = ProfileEvent.UNBLOCK_FAIL
                is ApiSuccess -> {
                    _event.value = ProfileEvent.UNBLOCK_SUCCESS
                    refresh()
                }
            }
        }
    }

    fun removeEvent() {
        _event.value = null
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
