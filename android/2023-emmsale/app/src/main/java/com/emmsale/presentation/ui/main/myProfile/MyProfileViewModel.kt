package com.emmsale.presentation.ui.main.myProfile

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
import com.emmsale.presentation.ui.main.myProfile.uiState.ActivityUiState
import com.emmsale.presentation.ui.main.myProfile.uiState.MyProfileScreenUiState
import kotlinx.coroutines.launch

class MyProfileViewModel(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val activityRepository: ActivityRepository,
) : ViewModel() {

    private val _uiState = NotNullMutableLiveData(MyProfileScreenUiState.Loading)
    val uiState: NotNullLiveData<MyProfileScreenUiState> = _uiState

    fun fetchMember() {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _uiState.postValue(_uiState.value.copy(isNotLogin = true))
                return@launch
            }
            launch {
                when (val result = memberRepository.getMember(token.uid)) {
                    is ApiError -> changeProfileFetchingErrorState()
                    is ApiException -> changeProfileFetchingErrorState()
                    is ApiSuccess -> setMember(result.data)
                }
            }
            launch {
                when (val result = activityRepository.getActivities(token.uid)) {
                    is ApiError -> changeProfileFetchingErrorState()
                    is ApiException -> changeProfileFetchingErrorState()
                    is ApiSuccess -> setActivities(result.data)
                }
            }
        }
    }

    private fun changeProfileFetchingErrorState() {
        _uiState.value = uiState.value.copy(
            isLoading = false,
            isProfileFetchingError = true,
        )
    }

    private fun setMember(member: Member) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isProfileFetchingError = false,
            memberId = member.id,
            memberName = member.name,
            description = member.description,
            memberImageUrl = member.imageUrl,
        )
    }

    private fun setActivities(activities: List<Activity>) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isProfileFetchingError = false,
            fields = activities.getActivityUiStatesOf(ActivityType.FIELD),
            educations = activities.getActivityUiStatesOf(ActivityType.EDUCATION),
            clubs = activities.getActivityUiStatesOf(ActivityType.CLUB),
        )
    }

    private fun List<Activity>.getActivityUiStatesOf(activityType: ActivityType): List<ActivityUiState> {
        return this.filter { it.activityType == activityType }
            .map { ActivityUiState.from(it) }
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
