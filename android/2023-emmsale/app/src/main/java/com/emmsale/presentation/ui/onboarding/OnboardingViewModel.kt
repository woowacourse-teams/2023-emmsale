package com.emmsale.presentation.ui.onboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.activity.ActivityRepository
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.member.MemberRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.onboarding.uistate.MemberSavingUiState
import com.emmsale.presentation.ui.onboarding.uistate.OnboardingUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val activityRepository: ActivityRepository,
    private val memberRepository: MemberRepository,
) : ViewModel() {
    val name: MutableLiveData<String> = MutableLiveData("")

    private val _activities = NotNullMutableLiveData(OnboardingUiState())
    val activities: NotNullLiveData<OnboardingUiState> = _activities

    val isExceedFieldLimit: Boolean
        get() = activities.value.fields.count { it.isSelected } == MAXIMUM_FIELD_SELECTION

    init {
        _activities.value = _activities.value.copy(isLoading = true)
        fetchActivities()
    }

    private fun fetchActivities(): Job = viewModelScope.launch {
        when (val activitiesResult = activityRepository.getActivities()) {
            is ApiSuccess -> _activities.postValue(OnboardingUiState.from(activitiesResult.data))
            is ApiError, is ApiException ->
                _activities.postValue(_activities.value.copy(isLoadingActivitiesFailed = true))
        }
    }

    fun updateSelection(tagId: Long, isSelected: Boolean) {
        val fields = _activities.value.fields
            .map { if (tagId == it.id) it.copy(isSelected = isSelected) else it }
        val educations = _activities.value.educations
            .map { if (tagId == it.id) it.copy(isSelected = isSelected) else it }
        val clubs = _activities.value.clubs
            .map { if (tagId == it.id) it.copy(isSelected = isSelected) else it }

        _activities.value = _activities.value.copy(
            fields = fields,
            educations = educations,
            clubs = clubs,
        )
    }

    fun updateMember() {
        viewModelScope.launch {
            _activities.value = _activities.value.copy(isLoading = true)

            when (
                memberRepository.updateMember(name.value!!, _activities.value.selectedActivityIds)
            ) {
                is ApiSuccess -> updateMemberSavingUiState(MemberSavingUiState.Success)
                is ApiError -> updateMemberSavingUiState(MemberSavingUiState.Failed)
                is ApiException -> updateMemberSavingUiState(MemberSavingUiState.Failed)
            }
        }
    }

    private fun updateMemberSavingUiState(memberSaving: MemberSavingUiState) {
        _activities.value = _activities.value.copy(memberSavingUiState = memberSaving)
    }

    companion object {
        private const val MAXIMUM_FIELD_SELECTION = 4

        val factory = ViewModelFactory {
            OnboardingViewModel(
                activityRepository = KerdyApplication.repositoryContainer.activityRepository,
                memberRepository = KerdyApplication.repositoryContainer.memberRepository,
            )
        }
    }
}
