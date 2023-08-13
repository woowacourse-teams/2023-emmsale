package com.emmsale.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.activity.Activity
import com.emmsale.data.activity.ActivityRepository
import com.emmsale.data.activity.ActivityType
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.member.Member
import com.emmsale.data.member.MemberRepository
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.profile.uiState.ActivityUiState
import com.emmsale.presentation.ui.profile.uiState.ProfileUiState
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val activityRepository: ActivityRepository,
) : ViewModel() {

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _profile = NotNullMutableLiveData(ProfileUiState.Loading)
    val profile: NotNullLiveData<ProfileUiState> = _profile

    fun fetchMember(memberId: Long) {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                changeToNotLoginState()
                return@launch
            }
            launch {
                when (val result = memberRepository.getMember(memberId)) {
                    is ApiError, is ApiException -> changeToProfileFetchingErrorState()
                    is ApiSuccess -> setMemberState(result.data, token.uid)
                }
            }
            launch {
                when (val result = activityRepository.getActivities(memberId)) {
                    is ApiError, is ApiException -> changeToProfileFetchingErrorState()
                    is ApiSuccess -> setActivitiesState(result.data)
                }
            }
        }
    }

    private fun changeToNotLoginState() {
        _isLogin.value = false
    }

    private fun setMemberState(member: Member, loginMemberId: Long) {
        _profile.value = _profile.value.copy(
            isLoading = false,
            isFetchingError = false,
            isLoginMember = member.id == loginMemberId,
            memberId = member.id,
            memberName = member.name,
            description = member.description,
            memberImageUrl = member.imageUrl,
        )
    }

    private fun setActivitiesState(activities: List<Activity>) {
        _profile.value = _profile.value.copy(
            isLoading = false,
            isFetchingError = false,
            fields = activities.getActivityUiStatesOf(ActivityType.FIELD),
            educations = activities.getActivityUiStatesOf(ActivityType.EDUCATION),
            clubs = activities.getActivityUiStatesOf(ActivityType.CLUB),
        )
    }

    private fun List<Activity>.getActivityUiStatesOf(activityType: ActivityType): List<ActivityUiState> {
        return this.filter { it.activityType == activityType }
            .map { ActivityUiState.from(it) }
    }

    private fun changeToProfileFetchingErrorState() {
        _profile.value = profile.value.copy(
            isLoading = false,
            isFetchingError = true,
        )
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
