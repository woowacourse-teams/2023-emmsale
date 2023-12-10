package com.emmsale.presentation.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.emmsale.data.model.ActivityType
import com.emmsale.data.repository.interfaces.ActivityRepository
import com.emmsale.data.repository.interfaces.ConfigRepository
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import com.emmsale.presentation.ui.onboarding.uiState.ActivityUiState
import com.emmsale.presentation.ui.onboarding.uiState.OnboardingUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val memberRepository: MemberRepository,
    private val configRepository: ConfigRepository,
) : RefreshableViewModel() {

    val name: MutableLiveData<String> = MutableLiveData("")

    private val _activities = NotNullMutableLiveData(listOf<ActivityUiState>())

    val fields: LiveData<List<ActivityUiState>> =
        _activities.map { activities -> activities.filter { activity -> activity.activity.activityType == ActivityType.INTEREST_FIELD } }

    val educations: LiveData<List<ActivityUiState>> =
        _activities.map { activities -> activities.filter { activity -> activity.activity.activityType == ActivityType.EDUCATION } }

    val clubs: LiveData<List<ActivityUiState>> =
        _activities.map { activities -> activities.filter { activity -> activity.activity.activityType == ActivityType.CLUB } }

    private val _canSubmit = NotNullMutableLiveData(true)
    val canSubmit: NotNullLiveData<Boolean> = _canSubmit

    private val _uiEvent = SingleLiveEvent<OnboardingUiEvent>()
    val uiEvent: LiveData<OnboardingUiEvent> = _uiEvent

    init {
        fetchActivities()
    }

    private fun fetchActivities(): Job = fetchData(
        fetchData = { activityRepository.getActivities() },
        onSuccess = { _activities.value = it.map(::ActivityUiState) },
    )

    override fun refresh(): Job = refreshData(
        refresh = { activityRepository.getActivities() },
        onSuccess = { _activities.value = it.map(::ActivityUiState) },
    )

    fun updateSelection(tagId: Long, isSelected: Boolean) {
        val newActivities = _activities.value
            .map { if (it.activity.id == tagId) it.copy(isSelected = isSelected) else it }

        if (newActivities.isExceedFieldLimit()) {
            _uiEvent.value = OnboardingUiEvent.FieldLimitExceedChecked
            _activities.value = _activities.value
            return
        }

        _activities.value = newActivities
    }

    private fun List<ActivityUiState>.isExceedFieldLimit(): Boolean =
        count { it.activity.activityType == ActivityType.INTEREST_FIELD && it.isSelected } > MAXIMUM_FIELD_SELECTION

    fun join(): Job = command(
        command = {
            memberRepository.createMember(
                name = name.value!!,
                activityIds = _activities.value.filter { it.isSelected }.map { it.activity.id },
            )
        },
        onSuccess = {
            configRepository.saveAutoLoginConfig(true)
            _uiEvent.value = OnboardingUiEvent.JoinComplete
        },
        onFailure = { _, _ -> _uiEvent.value = OnboardingUiEvent.JoinFail },
        onStart = { _canSubmit.value = false },
        onFinish = { _canSubmit.value = true },
    )

    companion object {
        private const val MAXIMUM_FIELD_SELECTION = 4
    }
}
