package com.emmsale.presentation.ui.main.myProfile

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
import com.emmsale.presentation.ui.main.myProfile.uiState.MyProfileErrorEvent
import com.emmsale.presentation.ui.main.myProfile.uiState.MyProfileUiState
import kotlinx.coroutines.launch

class MyProfileViewModel(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val activityRepository: ActivityRepository,
) : ViewModel() {

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _myProfile = NotNullMutableLiveData(MyProfileUiState())
    val myProfile: NotNullLiveData<MyProfileUiState> = _myProfile

    private val _errorEvents = MutableLiveData<MyProfileErrorEvent?>(null)
    val errorEvents: LiveData<MyProfileErrorEvent?> = _errorEvents

    fun fetchMember() {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }
            launch {
                when (val result = memberRepository.getMember(token.uid)) {
                    is ApiError, is ApiException ->
                        _errorEvents.value =
                            MyProfileErrorEvent.PROFILE_FETCHING

                    is ApiSuccess ->
                        _myProfile.value = _myProfile.value.changeMemberState(result.data)
                }
            }
            launch {
                when (val result = activityRepository.getActivities(token.uid)) {
                    is ApiError, is ApiException ->
                        _errorEvents.value =
                            MyProfileErrorEvent.PROFILE_FETCHING

                    is ApiSuccess ->
                        _myProfile.value = _myProfile.value.changeActivitiesState(result.data)
                }
            }
        }
    }

    fun removeErrorEvent() {
        _errorEvents.value = null
    }

    companion object {
        val factory = ViewModelFactory {
            MyProfileViewModel(
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                memberRepository = KerdyApplication.repositoryContainer.memberRepository,
                activityRepository = KerdyApplication.repositoryContainer.activityRepository,
            )
        }
    }
}
