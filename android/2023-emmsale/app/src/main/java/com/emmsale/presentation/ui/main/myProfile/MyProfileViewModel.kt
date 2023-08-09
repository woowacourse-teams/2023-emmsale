package com.emmsale.presentation.ui.main.myProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.emmsale.presentation.ui.main.myProfile.uiState.ActivityUiState
import com.emmsale.presentation.ui.main.myProfile.uiState.MyProfileScreenUiState
import kotlinx.coroutines.launch

class MyProfileViewModel(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val activityRepository: ActivityRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData(MyProfileScreenUiState.Loading)
    val uiState: LiveData<MyProfileScreenUiState> = _uiState

    fun fetchMember() {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _uiState.postValue(_uiState.value!!.copy(isNotLogin = true))
                return@launch
            }
            launch {
                when (val result = memberRepository.getMember(token.uid)) {
                    is ApiError -> changeErrorUiState(result.message.toString())
                    is ApiException -> changeErrorUiState(result.e.message.toString())
                    is ApiSuccess -> setMemberOnUi(result.data)
                }
            }
            launch {
                when (val result = activityRepository.getActivities(token.uid)) {
                    is ApiError -> changeErrorUiState(result.message.toString())
                    is ApiException -> changeErrorUiState(result.e.message.toString())
                    is ApiSuccess -> setActivitiesOnUi(result.data)
                }
            }
        }
    }

    fun onErrorMessageViewed() {
        _uiState.postValue(
            uiState.value!!.copy(
                isError = false,
                errorMessage = "",
            ),
        )
    }

    private fun changeErrorUiState(errorMessage: String) {
        _uiState.postValue(
            uiState.value!!.copy(
                isLoading = false,
                isError = true,
                errorMessage = errorMessage,
            ),
        )
    }

    private fun setMemberOnUi(member: Member) {
        _uiState.value = _uiState.value!!.copy(
            isLoading = false,
            isError = false,
            memberId = member.id,
            memberName = member.name,
            description = member.description,
            memberImageUrl = member.imageUrl,
        )
    }

    private fun setActivitiesOnUi(activities: List<Activity>) {
        _uiState.value = _uiState.value!!.copy(
            isLoading = false,
            isError = false,
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
