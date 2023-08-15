package com.emmsale.presentation.ui.main.myProfile.editMyProfile

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
import com.emmsale.presentation.common.livedata.error.ErrorPopLiveData
import com.emmsale.presentation.common.livedata.error.ErrorSetLiveData
import com.emmsale.presentation.ui.main.myProfile.editMyProfile.uiState.EditMyProfileErrorEvent
import com.emmsale.presentation.ui.main.myProfile.editMyProfile.uiState.EditMyProfileUiState
import kotlinx.coroutines.launch

class EditMyProfileViewModel(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val activityRepository: ActivityRepository,
) : ViewModel() {

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _profile = NotNullMutableLiveData(EditMyProfileUiState.FIRST_LOADING)
    val profile: NotNullLiveData<EditMyProfileUiState> = _profile

    private val _errorEvents = ErrorSetLiveData<EditMyProfileErrorEvent>()
    val errorEvents: ErrorPopLiveData<EditMyProfileErrorEvent> = _errorEvents

    fun fetchMember() {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }
            launch {
                when (val result = memberRepository.getMember(token.uid)) {
                    is ApiError, is ApiException -> _errorEvents.add(EditMyProfileErrorEvent.MEMBER_FETCHING)
                    is ApiSuccess -> _profile.value = _profile.value.changeMemberState(result.data)
                }
            }
            launch {
                when (val result = activityRepository.getActivities(token.uid)) {
                    is ApiError, is ApiException -> _errorEvents.add(EditMyProfileErrorEvent.MEMBER_FETCHING)
                    is ApiSuccess -> _profile.value = _profile.value.changeActivities(result.data)
                }
            }
        }
    }

    fun updateDescription(description: String) {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }
            when (memberRepository.updateMemberDescription(description)) {
                is ApiError, is ApiException -> _errorEvents.add(EditMyProfileErrorEvent.DESCRIPTION_UPDATE)
                is ApiSuccess -> _profile.value = _profile.value.changeDescription(description)
            }
        }
    }

    fun removeActivity(activityId: Long) {
    }

    companion object {
        val factory = ViewModelFactory {
            EditMyProfileViewModel(
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                memberRepository = KerdyApplication.repositoryContainer.memberRepository,
                activityRepository = KerdyApplication.repositoryContainer.activityRepository,
            )
        }
    }
}
