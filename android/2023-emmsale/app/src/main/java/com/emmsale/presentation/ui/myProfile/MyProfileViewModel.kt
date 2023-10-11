package com.emmsale.presentation.ui.myProfile

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
import com.emmsale.presentation.ui.myProfile.uiState.MyProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val activityRepository: ActivityRepository,
) : ViewModel(), Refreshable {

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _myProfile = NotNullMutableLiveData(MyProfileUiState())
    val myProfile: NotNullLiveData<MyProfileUiState> = _myProfile

    override fun refresh() {
        _myProfile.value = _myProfile.value.changeToLoadingState()
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }
            launch {
                when (val result = memberRepository.getMember(token.uid)) {
                    is Failure, NetworkError ->
                        _myProfile.value = _myProfile.value.changeToErrorState()

                    is Success -> _myProfile.value = _myProfile.value.changeMemberState(result.data)
                    is Unexpected -> throw Throwable(result.error)
                }
            }
            launch {
                when (val result = activityRepository.getActivities(token.uid)) {
                    is Failure, NetworkError ->
                        _myProfile.value = _myProfile.value.changeToErrorState()

                    is Success ->
                        _myProfile.value = _myProfile.value.changeActivitiesState(result.data)

                    is Unexpected -> throw Throwable(result.error)
                }
            }
        }
    }
}
