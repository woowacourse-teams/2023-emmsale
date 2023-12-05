package com.emmsale.presentation.ui.editMyProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.emmsale.data.model.Member
import com.emmsale.data.repository.interfaces.ActivityRepository
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import com.emmsale.presentation.ui.editMyProfile.uiState.ActivitiesUiState
import com.emmsale.presentation.ui.editMyProfile.uiState.EditMyProfileUiEvent
import com.emmsale.presentation.ui.editMyProfile.uiState.EditMyProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditMyProfileViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val activityRepository: ActivityRepository,
) : RefreshableViewModel() {

    private val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _profile = NotNullMutableLiveData(EditMyProfileUiState(Member()))
    val profile: NotNullLiveData<EditMyProfileUiState> = _profile

    private val _uiEvent = SingleLiveEvent<EditMyProfileUiEvent>()
    val uiEvent: LiveData<EditMyProfileUiEvent> = _uiEvent

    private val _activities = NotNullMutableLiveData(ActivitiesUiState())
    val activities: NotNullLiveData<ActivitiesUiState> = _activities

    init {
        fetchProfile()
    }

    private fun fetchProfile(): Job = fetchData(
        fetchData = { memberRepository.getMember(uid) },
        onSuccess = { _profile.value = EditMyProfileUiState(it) },
    )

    override fun refresh(): Job = refreshData(
        refresh = { memberRepository.getMember(uid) },
        onSuccess = { _profile.value = EditMyProfileUiState(it) },
    )

    fun fetchUnselectedActivities(): Job = fetchData(
        fetchData = { activityRepository.getActivities() },
        onSuccess = {
            _activities.value = _activities.value.fetchUnselectedActivities(
                allActivities = it,
                myActivities = profile.value.member.activities,
            )
        },
    )

    fun updateProfileImage(profileImageFile: File): Job = command(
        command = { memberRepository.updateMemberProfileImage(uid, profileImageFile) },
        onSuccess = { _profile.value = _profile.value.updateProfileImageUrl(it) },
        onFailure = { _, _ -> _uiEvent.value = EditMyProfileUiEvent.ProfileImageUpdateFail },
    )

    fun updateDescription(description: String): Job = command(
        command = { memberRepository.updateMemberDescription(description) },
        onSuccess = { _profile.value = _profile.value.changeDescription(description) },
        onFailure = { _, _ -> _uiEvent.value = EditMyProfileUiEvent.DescriptionUpdateFail },
    )

    fun removeActivity(activityId: Long): Job = commandAndRefresh(
        command = { memberRepository.deleteMemberActivities(listOf(activityId)) },
        onFailure = { _, _ -> _uiEvent.value = EditMyProfileUiEvent.ActivityRemoveFail },
    )

    fun addSelectedFields() {
        viewModelScope.launch {
            val selectedFieldIds =
                activities.value.fields.filter { it.isSelected }.map { it.activity.id }
            updateMemberActivities(selectedFieldIds)
        }
    }

    fun addSelectedEducations() {
        viewModelScope.launch {
            val selectedEducationIds =
                activities.value.educations.filter { it.isSelected }.map { it.activity.id }
            updateMemberActivities(selectedEducationIds)
        }
    }

    fun addSelectedClubs() {
        viewModelScope.launch {
            val selectedClubIds =
                activities.value.clubs.filter { it.isSelected }.map { it.activity.id }
            updateMemberActivities(selectedClubIds)
        }
    }

    private suspend fun updateMemberActivities(activityIds: List<Long>): Job = commandAndRefresh(
        command = { memberRepository.addMemberActivities(activityIds) },
        onFailure = { _, _ -> _uiEvent.value = EditMyProfileUiEvent.ActivitiesAddFail },
    )

    fun toggleActivitySelection(activityId: Long) {
        _activities.value = activities.value.toggleIsSelected(activityId)
    }
}
