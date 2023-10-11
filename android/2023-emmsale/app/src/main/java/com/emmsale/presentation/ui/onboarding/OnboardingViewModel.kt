package com.emmsale.presentation.ui.onboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.ActivityRepository
import com.emmsale.data.repository.interfaces.ConfigRepository
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.onboarding.uiState.MemberSavingUiState
import com.emmsale.presentation.ui.onboarding.uiState.OnboardingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val memberRepository: MemberRepository,
    private val configRepository: ConfigRepository,
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
        when (val result = activityRepository.getActivities()) {
            is Failure, NetworkError ->
                _activities.postValue(activities.value.copy(isLoadingActivitiesFailed = true))

            is Success -> _activities.postValue(OnboardingUiState.from(result.data))
            is Unexpected -> throw Throwable(result.error)
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
                val result = memberRepository.updateMember(
                    name.value!!,
                    _activities.value.selectedActivityIds,
                )
            ) {
                is Failure, NetworkError -> updateMemberSavingUiState(MemberSavingUiState.Failed)
                is Success -> {
                    updateMemberSavingUiState(MemberSavingUiState.Success)
                    configRepository.saveAutoLoginConfig(true)
                }

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    private fun updateMemberSavingUiState(memberSaving: MemberSavingUiState) {
        _activities.value = _activities.value.copy(memberSavingUiState = memberSaving)
    }

    companion object {
        private const val MAXIMUM_FIELD_SELECTION = 4
    }
}
