package com.emmsale.presentation.ui.main.myProfile.editMyProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.emmsale.data.activity.Activity
import com.emmsale.data.activity.ActivityRepository
import com.emmsale.data.activity.ActivityType
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.member.MemberRepository
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.error.ErrorPopLiveData
import com.emmsale.presentation.common.livedata.error.ErrorSetLiveData
import com.emmsale.presentation.ui.main.myProfile.editMyProfile.uiState.EditMyProfileErrorEvent
import com.emmsale.presentation.ui.main.myProfile.editMyProfile.uiState.EditMyProfileUiState
import com.emmsale.presentation.ui.main.myProfile.editMyProfile.uiState.SelectableActivityUiState
import kotlinx.coroutines.launch

class EditMyProfileViewModel(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val activityRepository: ActivityRepository,
) : ViewModel() {

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _profile = NotNullMutableLiveData(EditMyProfileUiState.FIRST_LOADING)
    val profile: NotNullLiveData<EditMyProfileUiState> = _profile

    private val _errorEvents = ErrorSetLiveData<EditMyProfileErrorEvent>()
    val errorEvents: ErrorPopLiveData<EditMyProfileErrorEvent> = _errorEvents

    private val _selectableFields = NotNullMutableLiveData(listOf<SelectableActivityUiState>())
    val selectableFields: NotNullLiveData<List<SelectableActivityUiState>> = _selectableFields

    val selectedFieldsSize = _selectableFields.map { fields -> fields.count { it.isSelected } }

    val selectableFieldsCount =
        _selectableFields.map { maxOf(MAX_FIELDS_COUNT - profile.value.fields.size, 0) }

    private val _selectableEducations = NotNullMutableLiveData(listOf<SelectableActivityUiState>())
    val selectableEducations: NotNullLiveData<List<SelectableActivityUiState>> =
        _selectableEducations

    val selectedEducationsSize =
        _selectableEducations.map { education -> education.count { it.isSelected } }

    private val _selectableClubs = NotNullMutableLiveData(listOf<SelectableActivityUiState>())
    val selectableClubs: NotNullLiveData<List<SelectableActivityUiState>> = _selectableClubs

    val selectedClubsSize = _selectableClubs.map { club -> club.count { it.isSelected } }

    fun fetchMember() {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }
            launch {
                when (val result = memberRepository.getMember(token.uid)) {
                    is ApiError, is ApiException -> _errorEvents.add(EditMyProfileErrorEvent.MEMBER_FETCHING)
                    is ApiSuccess -> _profile.value = _profile.value.changeMemberState(result.data)
                }
            }
            launch {
                when (val result = activityRepository.getActivities(token.uid)) {
                    is ApiError, is ApiException -> _errorEvents.add(EditMyProfileErrorEvent.MEMBER_FETCHING)
                    is ApiSuccess -> _profile.value = _profile.value.changeActivities(result.data)
                }
            }
        }
    }

    fun updateDescription(description: String) {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }
            when (memberRepository.updateMemberDescription(description)) {
                is ApiError, is ApiException -> _errorEvents.add(EditMyProfileErrorEvent.DESCRIPTION_UPDATE)
                is ApiSuccess -> _profile.value = _profile.value.changeDescription(description)
            }
        }
    }

    fun removeActivity(activityId: Long) {
    }

    fun addSelectedFields() {
    }

    fun addSelectedEducations() {
    }

    fun addSelectedClubs() {}

    fun setFieldSelection(activityId: Long, isSelected: Boolean) {
        _selectableFields.value =
            _selectableFields.value.map { if (it.id == activityId) it.copy(isSelected = isSelected) else it }
    }

    fun setEducationSelection(activityId: Long, isSelected: Boolean) {
        _selectableEducations.value =
            _selectableEducations.value.map { if (it.id == activityId) it.copy(isSelected = isSelected) else it }
    }

    fun setClubSelection(activityId: Long, isSelected: Boolean) {
        _selectableClubs.value =
            _selectableClubs.value.map { if (it.id == activityId) it.copy(isSelected = isSelected) else it }
    }

    fun fetchFields() {
        if (_selectableFields.value.isNotEmpty()) return
        viewModelScope.launch {
            when (val result = activityRepository.getActivities()) {
                is ApiError, is ApiException -> _errorEvents.add(EditMyProfileErrorEvent.FIELDS_FETCHING)
                is ApiSuccess -> _selectableFields.value = result.data.toSelectableFields()
            }
        }
    }

    private fun List<Activity>.toSelectableFields(): List<SelectableActivityUiState> =
        filter { activity -> activity.activityType == ActivityType.FIELD && activity.id !in profile.value.fields.map { it.id } }
            .map { SelectableActivityUiState.from(it) }

    fun fetchEducations() {
        if (_selectableEducations.value.isNotEmpty()) return
        viewModelScope.launch {
            when (val result = activityRepository.getActivities()) {
                is ApiError, is ApiException -> _errorEvents.add(EditMyProfileErrorEvent.EDUCATIONS_FETCHING)
                is ApiSuccess -> _selectableEducations.value = result.data.toSelectableEducations()
            }
        }
    }

    private fun List<Activity>.toSelectableEducations(): List<SelectableActivityUiState> =
        filter { activity -> activity.activityType == ActivityType.EDUCATION && activity.id !in profile.value.educations.map { it.id } }
            .map { SelectableActivityUiState.from(it) }

    fun fetchClubs() {
        if (_selectableClubs.value.isNotEmpty()) return
        viewModelScope.launch {
            when (val result = activityRepository.getActivities()) {
                is ApiError, is ApiException -> _errorEvents.add(EditMyProfileErrorEvent.CLUBS_FETCHING)
                is ApiSuccess -> _selectableClubs.value = result.data.toSelectableClubs()
            }
        }
    }

    private fun List<Activity>.toSelectableClubs(): List<SelectableActivityUiState> =
        filter { activity -> activity.activityType == ActivityType.CLUB && activity.id !in profile.value.clubs.map { it.id } }
            .map { SelectableActivityUiState.from(it) }

    companion object {
        private const val MAX_FIELDS_COUNT = 4

        val factory = ViewModelFactory {
            EditMyProfileViewModel(
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                memberRepository = KerdyApplication.repositoryContainer.memberRepository,
                activityRepository = KerdyApplication.repositoryContainer.activityRepository,
            )
        }
    }
}
