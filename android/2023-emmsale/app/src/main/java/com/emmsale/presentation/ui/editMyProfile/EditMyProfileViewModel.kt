package com.emmsale.presentation.ui.editMyProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.ActivityRepository
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.editMyProfile.uiState.ActivitiesUiState
import com.emmsale.presentation.ui.editMyProfile.uiState.EditMyProfileErrorEvent
import com.emmsale.presentation.ui.editMyProfile.uiState.EditMyProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditMyProfileViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val activityRepository: ActivityRepository,
) : ViewModel(), Refreshable {

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _profile = NotNullMutableLiveData(EditMyProfileUiState.FIRST_LOADING)
    val profile: NotNullLiveData<EditMyProfileUiState> = _profile

    private val _errorEvents = MutableLiveData<EditMyProfileErrorEvent?>(null)
    val errorEvents: LiveData<EditMyProfileErrorEvent?> = _errorEvents

    private val _activities = NotNullMutableLiveData(ActivitiesUiState())
    val activities: NotNullLiveData<ActivitiesUiState> = _activities

    init {
        refresh()
    }

    override fun refresh() {
        val token = tokenRepository.getToken()
        if (token == null) {
            _isLogin.value = false
            return
        }
        _profile.value = _profile.value.copy(isLoading = false)
        viewModelScope.launch {
            when (val result = memberRepository.getMember(token.uid)) {
                is Success -> _profile.value = _profile.value.changeMemberState(result.data)
                is Failure, NetworkError -> _profile.value = _profile.value.changeToErrorState()
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun fetchAllActivities() {
        viewModelScope.launch {
            when (val result = activityRepository.getActivities()) {
                is Failure, NetworkError ->
                    _activities.value =
                        _activities.value.changeToErrorState()

                is Success -> {
                    _activities.value = _activities.value.fetchUnSelectedActivities(
                        allActivities = result.data,
                        myActivities = profile.value.member.activities,
                    )
                }

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun updateProfileImage(profileImageUrl: String) {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }
            when (
                val result =
                    memberRepository.updateMemberProfileImage(token.uid, profileImageUrl)
            ) {
                is Success -> _profile.value = _profile.value.updateProfileImageUrl(result.data)

                is Failure, NetworkError ->
                    _errorEvents.value = EditMyProfileErrorEvent.PROFILE_IMAGE_UPDATE

                is Unexpected -> throw Throwable(result.error)
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
            when (val result = memberRepository.updateMemberDescription(description)) {
                is Failure, NetworkError ->
                    _errorEvents.value =
                        EditMyProfileErrorEvent.DESCRIPTION_UPDATE

                is Success -> _profile.value = _profile.value.changeDescription(description)
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun removeActivity(activityId: Long) {
        viewModelScope.launch {
            when (val result = memberRepository.deleteMemberActivities(listOf(activityId))) {
                is Failure, NetworkError ->
                    _errorEvents.value =
                        EditMyProfileErrorEvent.ACTIVITY_REMOVE

                is Success -> refresh()
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

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

    private suspend fun updateMemberActivities(activityIds: List<Long>) {
        when (val result = memberRepository.addMemberActivities(activityIds)) {
            is Failure, NetworkError ->
                _errorEvents.value =
                    EditMyProfileErrorEvent.ACTIVITIES_ADD

            is Success -> refresh()
            is Unexpected -> throw Throwable(result.error)
        }
    }

    fun toggleActivitySelection(activityId: Long) {
        _activities.value = activities.value.toggleIsSelected(activityId)
    }

    fun removeError() {
        _errorEvents.value = null
    }
}
