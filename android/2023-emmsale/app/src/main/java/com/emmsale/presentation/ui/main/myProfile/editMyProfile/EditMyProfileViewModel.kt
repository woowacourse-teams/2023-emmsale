package com.emmsale.presentation.ui.main.myProfile.editMyProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.main.myProfile.editMyProfile.uiState.EditMyProfileErrorEvent
import com.emmsale.presentation.ui.main.myProfile.editMyProfile.uiState.EditMyProfileUiState
import com.emmsale.presentation.ui.main.myProfile.editMyProfile.uiState.SelectableActivityUiState
import kotlinx.coroutines.launch

class EditMyProfileViewModel(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val activityRepository: ActivityRepository,
) : ViewModel(), Refreshable {

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _isLoading = NotNullMutableLiveData(false)
    val isLoading: NotNullLiveData<Boolean> = _isLoading

    private val _isError = NotNullMutableLiveData(false)
    val isError: NotNullLiveData<Boolean> = _isError

    private val _profile = NotNullMutableLiveData(EditMyProfileUiState.FIRST_LOADING)
    val profile: NotNullLiveData<EditMyProfileUiState> = _profile

    private val _errorEvents = MutableLiveData<EditMyProfileErrorEvent?>(null)
    val errorEvents: LiveData<EditMyProfileErrorEvent?> = _errorEvents

    private val _selectableFields = NotNullMutableLiveData(listOf<SelectableActivityUiState>())
    val selectableFields: NotNullLiveData<List<SelectableActivityUiState>> = _selectableFields

    val selectedFieldsSize = _selectableFields.map { fields -> fields.count { it.isSelected } }

    val selectableFieldsCount =
        _selectableFields.map { maxOf(MAX_FIELDS_COUNT - profile.value.fields.size, 0) }

    private val _selectableEducations = NotNullMutableLiveData(listOf<SelectableActivityUiState>())
    val selectableEducations: NotNullLiveData<List<SelectableActivityUiState>> =
        _selectableEducations

    val selectedEducationsSize =
        _selectableEducations.map { educations -> educations.count { it.isSelected } }

    private val _selectableClubs = NotNullMutableLiveData(listOf<SelectableActivityUiState>())
    val selectableClubs: NotNullLiveData<List<SelectableActivityUiState>> = _selectableClubs

    val selectedClubsSize = _selectableClubs.map { clubs -> clubs.count { it.isSelected } }

    init {
        refresh()
    }

    override fun refresh() {
        _isLoading.value = true
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }
            launch {
                when (val result = memberRepository.getMember(token.uid)) {
                    is ApiError, is ApiException -> _isError.value = true
                    is ApiSuccess -> {
                        _profile.value = _profile.value.changeMemberState(result.data)
                        _isLoading.value = false
                        _isError.value = false
                    }
                }
            }
            launch {
                when (val result = activityRepository.getActivities(token.uid)) {
                    is ApiError, is ApiException -> _isError.value = true
                    is ApiSuccess -> {
                        _profile.value = _profile.value.changeActivities(result.data)
                        _isLoading.value = false
                        _isError.value = false
                    }
                }
            }.join()
            fetchAllActivities()
        }
    }

    private fun fetchAllActivities() {
        viewModelScope.launch {
            when (val result = activityRepository.getActivities()) {
                is ApiError, is ApiException -> _isError.value = true
                is ApiSuccess -> {
                    _selectableFields.value = result.data.toSelectableFields()
                    _selectableEducations.value = result.data.toSelectableEducations()
                    _selectableClubs.value = result.data.toSelectableClubs()
                    _isLoading.value = false
                    _isError.value = false
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
                is ApiError, is ApiException ->
                    _errorEvents.value =
                        EditMyProfileErrorEvent.DESCRIPTION_UPDATE

                is ApiSuccess -> _profile.value = _profile.value.changeDescription(description)
            }
        }
    }

    fun removeActivity(activityId: Long) {
        viewModelScope.launch {
            when (memberRepository.deleteMemberActivities(listOf(activityId))) {
                is ApiError, is ApiException ->
                    _errorEvents.value =
                        EditMyProfileErrorEvent.ACTIVITY_REMOVE

                is ApiSuccess -> refresh()
            }
        }
    }

    fun addSelectedFields() {
        viewModelScope.launch {
            val selectedFieldIds = _selectableFields.value.filter { it.isSelected }.map { it.id }
            when (memberRepository.addMemberActivities(selectedFieldIds)) {
                is ApiError, is ApiException ->
                    _errorEvents.value =
                        EditMyProfileErrorEvent.ACTIVITIES_ADD

                is ApiSuccess -> refresh()
            }
        }
    }

    fun addSelectedEducations() {
        viewModelScope.launch {
            val selectedEducationIds =
                _selectableEducations.value.filter { it.isSelected }.map { it.id }
            when (memberRepository.addMemberActivities(selectedEducationIds)) {
                is ApiError, is ApiException ->
                    _errorEvents.value =
                        EditMyProfileErrorEvent.ACTIVITIES_ADD

                is ApiSuccess -> refresh()
            }
        }
    }

    fun addSelectedClubs() {
        viewModelScope.launch {
            val selectedClubIds = _selectableClubs.value.filter { it.isSelected }.map { it.id }
            when (memberRepository.addMemberActivities(selectedClubIds)) {
                is ApiError, is ApiException ->
                    _errorEvents.value =
                        EditMyProfileErrorEvent.ACTIVITIES_ADD

                is ApiSuccess -> refresh()
            }
        }
    }

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

    private fun List<Activity>.toSelectableFields(): List<SelectableActivityUiState> =
        filter { activity -> activity.activityType == ActivityType.FIELD && activity.id !in profile.value.fields.map { it.id } }
            .map { SelectableActivityUiState.from(it) }

    private fun List<Activity>.toSelectableEducations(): List<SelectableActivityUiState> =
        filter { activity -> activity.activityType == ActivityType.EDUCATION && activity.id !in profile.value.educations.map { it.id } }
            .map { SelectableActivityUiState.from(it) }

    private fun List<Activity>.toSelectableClubs(): List<SelectableActivityUiState> =
        filter { activity -> activity.activityType == ActivityType.CLUB && activity.id !in profile.value.clubs.map { it.id } }
            .map { SelectableActivityUiState.from(it) }

    fun removeError() {
        _errorEvents.value = null
    }

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
