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
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.ui.onboarding.uistate.ActivitiesUiState
import com.emmsale.presentation.ui.onboarding.uistate.ActivityTypeContentUiState
import com.emmsale.presentation.ui.onboarding.uistate.ActivityUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val activityRepository: ActivityRepository,
) : ViewModel() {
    val nameUiState: MutableLiveData<String> = MutableLiveData()

    // TODO("Error Handling on OnboardingActivity")
    private val _activities: MutableLiveData<ActivityTypeContentUiState> = MutableLiveData()
    private val selectedActivityIds: MutableList<Int> = mutableListOf()

    val educations: LiveData<ActivitiesUiState?> = _activities.map { activities ->
        when (activities) {
            is ActivityTypeContentUiState.Success -> activities.findActivity(ActivityCategory.EDUCATION)
            is ActivityTypeContentUiState.Error -> null
        }
    }

    val clubs: LiveData<ActivitiesUiState?> = _activities.map { activities ->
        when (activities) {
            is ActivityTypeContentUiState.Success -> activities.findActivity(ActivityCategory.CLUB)
            is ActivityTypeContentUiState.Error -> null
        }
    }

    val jobs: LiveData<ActivitiesUiState?> = _activities.map { activities ->
        when (activities) {
            is ActivityTypeContentUiState.Success -> activities.findActivity(ActivityCategory.JOB)
            is ActivityTypeContentUiState.Error -> null
        }
    }

    init {
        fetchActivities()
    }

    private fun fetchActivities(): Job = viewModelScope.launch {
        when (val activitiesApiModel = activityRepository.getActivities()) {
            is ApiSuccess ->
                _activities.postValue(ActivityTypeContentUiState.from(activitiesApiModel.data))

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

    companion object {
        val factory = ViewModelFactory {
            OnboardingViewModel(KerdyApplication.repositoryContainer.activityRepository)
        }
    }
}
