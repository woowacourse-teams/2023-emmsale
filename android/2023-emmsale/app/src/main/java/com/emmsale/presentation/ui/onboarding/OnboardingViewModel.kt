package com.emmsale.presentation.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.emmsale.data.activity.ActivityRepository
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.member.Member
import com.emmsale.data.member.MemberRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.ui.onboarding.uistate.ActivitiesUiState
import com.emmsale.presentation.ui.onboarding.uistate.ActivityTypeContentUiState
import com.emmsale.presentation.ui.onboarding.uistate.ActivityUiState
import com.emmsale.presentation.ui.onboarding.uistate.MemberUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val activityRepository: ActivityRepository,
    private val memberRepository: MemberRepository,
) : ViewModel() {
    val nameUiState: MutableLiveData<String> = MutableLiveData()

    // TODO("Error Handling on OnboardingActivity")
    private val _activities: MutableLiveData<ActivityTypeContentUiState> = MutableLiveData()
    private val selectedActivityIds: MutableList<Int> = mutableListOf()

    val educations: LiveData<ActivitiesUiState?> = _activities.map { activityTypeContent ->
        when (activityTypeContent) {
            is ActivityTypeContentUiState.Success ->
                findActivity(activityTypeContent, ActivityCategory.EDUCATION)

            is ActivityTypeContentUiState.Error -> null
        }
    }

    val clubs: LiveData<ActivitiesUiState?> = _activities.map { activityTypeContent ->
        when (activityTypeContent) {
            is ActivityTypeContentUiState.Success ->
                findActivity(activityTypeContent, ActivityCategory.CLUB)

            is ActivityTypeContentUiState.Error -> null
        }
    }

    val jobs: LiveData<ActivitiesUiState?> = _activities.map { activityTypeContent ->
        when (activityTypeContent) {
            is ActivityTypeContentUiState.Success ->
                findActivity(activityTypeContent, ActivityCategory.JOB)

            is ActivityTypeContentUiState.Error -> null
        }
    }

    private val _memberUiState = MutableLiveData<MemberUiState>()
    val memberUiState: LiveData<MemberUiState> = _memberUiState

    init {
        fetchActivities()
    }

    private fun fetchActivities(): Job = viewModelScope.launch {
        when (val activitiesResult = activityRepository.getActivities()) {
            is ApiSuccess ->
                _activities.postValue(ActivityTypeContentUiState.from(activitiesResult.data))

            is ApiError -> _activities.postValue(ActivityTypeContentUiState.Error)
            is ApiException -> _activities.postValue(ActivityTypeContentUiState.Error)
        }
    }

    fun toggleTagSelection(tag: ActivityUiState) {
        tag.isSelected = !tag.isSelected
        when (tag.isSelected) {
            true -> selectedActivityIds.add(tag.id)
            false -> selectedActivityIds.remove(tag.id)
        }
    }

    fun updateMember() {
        val memberName = nameUiState.value ?: return

        viewModelScope.launch {
            _memberUiState.value = MemberUiState.Loading
            val member = Member(memberName, selectedActivityIds)

            when (memberRepository.updateMember(member)) {
                is ApiSuccess -> _memberUiState.value = MemberUiState.Success
                is ApiError -> _memberUiState.value = MemberUiState.Failed
                is ApiException -> _memberUiState.value = MemberUiState.Failed
            }
        }
    }

    private fun findActivity(
        activityTypeContent: ActivityTypeContentUiState.Success,
        category: ActivityCategory
    ): ActivitiesUiState? = activityTypeContent.activities.find { it.category == category.title }

    companion object {
        val factory = ViewModelFactory {
            OnboardingViewModel(
                activityRepository = KerdyApplication.repositoryContainer.activityRepository,
                memberRepository = KerdyApplication.repositoryContainer.memberRepository
            )
        }
    }
}
