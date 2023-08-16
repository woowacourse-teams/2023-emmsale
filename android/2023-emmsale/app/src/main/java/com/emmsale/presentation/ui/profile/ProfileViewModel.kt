package com.emmsale.presentation.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.activity.ActivityRepository
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.member.MemberRepository
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.profile.uiState.ProfileEvent
import com.emmsale.presentation.ui.profile.uiState.ProfileUiState
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val activityRepository: ActivityRepository,
) : ViewModel() {

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _profile = NotNullMutableLiveData(ProfileUiState.FIRST_LOADING)
    val profile: NotNullLiveData<ProfileUiState> = _profile

    private val _event = MutableLiveData<ProfileEvent?>(null)
    val event: LiveData<ProfileEvent?> = _event

    fun fetchMember(memberId: Long) {
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
        }
    }

    fun blockMember(memberId: Long) {
        viewModelScope.launch {
            when (memberRepository.blockMember(memberId)) {
                is ApiError, is ApiException -> _event.value = ProfileEvent.BLOCK_FAIL
                is ApiSuccess -> _event.value = ProfileEvent.BLOCK_COMPLETE
            }
        }
    }

    fun removeEvent() {
        _event.value = null
    }

    companion object {
        val factory = ViewModelFactory {
            ProfileViewModel(
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                memberRepository = KerdyApplication.repositoryContainer.memberRepository,
                activityRepository = KerdyApplication.repositoryContainer.activityRepository,
            )
        }
    }
}
