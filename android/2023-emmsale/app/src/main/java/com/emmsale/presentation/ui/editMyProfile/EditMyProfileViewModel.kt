package com.emmsale.presentation.ui.editMyProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.emmsale.data.model.Activity
import com.emmsale.data.model.ActivityType
import com.emmsale.data.model.Member
import com.emmsale.data.repository.interfaces.ActivityRepository
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
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

    private val _activities = NotNullMutableLiveData(listOf<Activity>())
    val activities: NotNullLiveData<List<Activity>> = _activities

    val fields: LiveData<List<Activity>> = _activities.map {
        it.filter { activity -> activity.activityType == ActivityType.INTEREST_FIELD }
    }

    val clubs: LiveData<List<Activity>> = _activities.map {
        it.filter { activity -> activity.activityType == ActivityType.CLUB }
    }

    val educations: LiveData<List<Activity>> = _activities.map {
        it.filter { activity -> activity.activityType == ActivityType.EDUCATION }
    }

    private val _selectedActivityIds = NotNullMutableLiveData(setOf<Long>())

    val selectedFields = MediatorLiveData(setOf<Activity>()).apply {
        addSource(_selectedActivityIds) { value = getSelectedFields() }
        addSource(fields) { value = getSelectedFields() }
    }

    val selectedClubs = MediatorLiveData(setOf<Activity>()).apply {
        addSource(_selectedActivityIds) { value = getSelectedClubs() }
        addSource(clubs) { value = getSelectedClubs() }
    }

    val selectedEducations = MediatorLiveData(setOf<Activity>()).apply {
        addSource(_selectedActivityIds) { value = getSelectedEducations() }
        addSource(educations) { value = getSelectedEducations() }
    }

    val isFieldsSelectionChanged = MediatorLiveData(false).apply {
        addSource(_profile) { value = selectedFields.value != _profile.value.member.fields.toSet() }
        addSource(selectedFields) {
            value = selectedFields.value != _profile.value.member.fields.toSet()
        }
    }

    val isClubsSelectionChanged = MediatorLiveData(false).apply {
        addSource(_profile) { value = selectedClubs.value != _profile.value.member.clubs.toSet() }
        addSource(selectedClubs) {
            value = selectedClubs.value != _profile.value.member.clubs.toSet()
        }
    }

    val isEducationsSelectionChanged = MediatorLiveData(false).apply {
        addSource(_profile) {
            value = selectedEducations.value != _profile.value.member.educations.toSet()
        }
        addSource(selectedEducations) {
            value = selectedEducations.value != _profile.value.member.educations.toSet()
        }
    }

    private val _uiEvent = SingleLiveEvent<EditMyProfileUiEvent>()
    val uiEvent: LiveData<EditMyProfileUiEvent> = _uiEvent

    private fun getSelectedFields(): Set<Activity> =
        fields.value?.filter { it.id in _selectedActivityIds.value }?.toSet() ?: emptySet()

    private fun getSelectedClubs(): Set<Activity> =
        clubs.value?.filter { it.id in _selectedActivityIds.value }?.toSet() ?: emptySet()

    private fun getSelectedEducations(): Set<Activity> =
        educations.value?.filter { it.id in _selectedActivityIds.value }?.toSet() ?: emptySet()

    init {
        fetchProfile()
        fetchActivities()
    }

    private fun fetchProfile(): Job = fetchData(
        fetchData = { memberRepository.getMember(uid) },
        onSuccess = ::onProfileFetchSuccess,
    )

    private fun fetchActivities(): Job = fetchData(
        fetchData = { activityRepository.getActivities() },
        onSuccess = { _activities.value = it },
    )

    override fun refresh(): Job = refreshData(
        refresh = { memberRepository.getMember(uid) },
        onSuccess = ::onProfileFetchSuccess,
    )

    private fun onProfileFetchSuccess(member: Member) {
        _profile.value = EditMyProfileUiState(member)
        _selectedActivityIds.value = member.activities.map(Activity::id).toSet()
    }

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

    fun updateFields() {
        viewModelScope.launch {
            val addFieldIds = selectedFields.value
                ?.filter { it !in _profile.value.member.fields }
                ?.map(Activity::id)
                ?: emptyList()
            val removeFieldIds = _profile.value.member.fields
                .filter { it !in (selectedFields.value ?: emptyList()) }
                .map(Activity::id)
            if (addFieldIds.isNotEmpty()) addActivities(addFieldIds)
            if (removeFieldIds.isNotEmpty()) removeActivities(removeFieldIds)
        }
    }

    fun addSelectedEducations() {
        viewModelScope.launch {
            val addEducationIds = selectedEducations.value
                ?.filter { it !in _profile.value.member.educations }
                ?.map(Activity::id)
                ?: emptyList()
            val removeEducationIds = _profile.value.member.educations
                .filter { it !in (selectedEducations.value ?: emptyList()) }
                .map(Activity::id)
            if (addEducationIds.isNotEmpty()) addActivities(addEducationIds)
            if (removeEducationIds.isNotEmpty()) removeActivities(removeEducationIds)
        }
    }

    fun addSelectedClubs() {
        viewModelScope.launch {
            val addClubIds = selectedClubs.value
                ?.filter { it !in _profile.value.member.clubs }
                ?.map(Activity::id)
                ?: emptyList()
            val removeClubIds = _profile.value.member.clubs
                .filter { it !in (selectedClubs.value ?: emptyList()) }
                .map(Activity::id)
            if (addClubIds.isNotEmpty()) addActivities(addClubIds)
            if (removeClubIds.isNotEmpty()) removeActivities(removeClubIds)
        }
    }

    private suspend fun addActivities(activityIds: List<Long>): Job = commandAndRefresh(
        command = { memberRepository.addMemberActivities(activityIds) },
        onFailure = { _, _ -> _uiEvent.value = EditMyProfileUiEvent.ActivitiesAddFail },
    )

    private suspend fun removeActivities(activityIds: List<Long>): Job = commandAndRefresh(
        command = { memberRepository.deleteMemberActivities(activityIds) },
        onFailure = { _, _ -> _uiEvent.value = EditMyProfileUiEvent.ActivityRemoveFail },
    )

    fun setActivitySelection(activityId: Long, isSelected: Boolean) {
        if (isSelected) {
            _selectedActivityIds.value += activityId
        } else {
            _selectedActivityIds.value -= activityId
        }
    }

    companion object {
        const val MAX_FIELDS_COUNT = 4
    }
}
