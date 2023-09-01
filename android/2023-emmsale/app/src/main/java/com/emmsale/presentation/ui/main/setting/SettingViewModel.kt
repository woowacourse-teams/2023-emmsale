package com.emmsale.presentation.ui.main.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.BuildConfig
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.member.MemberRepository
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.RefreshableViewModel
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.main.setting.uiState.MemberUiState
import kotlinx.coroutines.launch

class SettingViewModel(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
) : ViewModel(), RefreshableViewModel {

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _member = NotNullMutableLiveData(MemberUiState.FIRST_LOADING)
    val member: NotNullLiveData<MemberUiState> = _member

    private val _appVersion = NotNullMutableLiveData(BuildConfig.VERSION_NAME)
    val appVersion: NotNullLiveData<String> = _appVersion

    init {
        refreshNotifications()
    }

    override fun refreshNotifications() {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }
            when (val result = memberRepository.getMember(token.uid)) {
                is ApiError, is ApiException ->
                    _member.value = _member.value.changeToErrorState()

                is ApiSuccess ->
                    _member.value =
                        _member.value.changeMemberState(result.data)
            }
        }
    }

    fun logout() {
        _member.value = _member.value.changeToLoadingState()
        viewModelScope.launch {
            tokenRepository.deleteToken()
            _member.value = _member.value.changeToLogoutState()
        }
    }

    companion object {
        val factory = ViewModelFactory {
            SettingViewModel(
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                memberRepository = KerdyApplication.repositoryContainer.memberRepository,
            )
        }
    }
}
