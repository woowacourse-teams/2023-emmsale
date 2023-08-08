package com.emmsale.presentation.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.activity.ActivityRepository
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.member.Member
import com.emmsale.data.member.MemberRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.ui.onboarding.uistate.MemberUiState
import com.emmsale.presentation.ui.onboarding.uistate.OnboardingUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val activityRepository: ActivityRepository,
    private val memberRepository: MemberRepository,
) : ViewModel() {
    val name: MutableLiveData<String> = MutableLiveData()

    // TODO("Error Handling on OnboardingActivity")
    private val _activities: MutableLiveData<OnboardingUiState> =
        MutableLiveData(OnboardingUiState())
    val activities: LiveData<OnboardingUiState> = _activities

    private val _memberUiState = MutableLiveData<MemberUiState>()
    val memberUiState: LiveData<MemberUiState> = _memberUiState

    init {
        _activities.value = _activities.value?.copy(isLoading = true)
        fetchActivities()
    }

    private fun fetchActivities(): Job = viewModelScope.launch {
        when (val activitiesResult = activityRepository.getActivities()) {
            is ApiSuccess -> _activities.postValue(OnboardingUiState.from(activitiesResult.data))
            is ApiError -> _activities.postValue(_activities.value?.copy(isError = true))
            is ApiException -> _activities.postValue(_activities.value?.copy(isError = true))
        }
    }

    fun updateSelection(tagId: Long, isSelected: Boolean) {
        val fields = _activities.value?.fields
            ?.map { if (tagId == it.id) it.copy(isSelected = isSelected) else it }
        val educations = _activities.value?.educations
            ?.map { if (tagId == it.id) it.copy(isSelected = isSelected) else it }
        val clubs = _activities.value?.clubs
            ?.map { if (tagId == it.id) it.copy(isSelected = isSelected) else it }

        _activities.value = _activities.value?.copy(
            fields = fields!!,
            educations = educations!!,
            clubs = clubs!!,
        )
    }

    fun updateMember() {
        val memberName = name.value ?: return

        viewModelScope.launch {
            _memberUiState.value = MemberUiState.Loading
            val member = Member(memberName, _activities.value!!.selectedActivityIds)

            when (memberRepository.updateMember(member)) {
                is ApiSuccess -> _memberUiState.value = MemberUiState.Success
                is ApiError -> _memberUiState.value = MemberUiState.Failed
                is ApiException -> _memberUiState.value = MemberUiState.Failed
            }
        }
    }

    companion object {
        val factory = ViewModelFactory {
            OnboardingViewModel(
                activityRepository = KerdyApplication.repositoryContainer.activityRepository,
                memberRepository = KerdyApplication.repositoryContainer.memberRepository,
            )
        }
    }
}
