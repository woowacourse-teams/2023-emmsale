package com.emmsale.presentation.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.member.MemberRepository
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.setting.uiState.MemberUiState
import kotlinx.coroutines.launch

class SettingViewModel(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
) : ViewModel() {

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _member = NotNullMutableLiveData(MemberUiState.FIRST_LOADING)
    val member: NotNullLiveData<MemberUiState> = _member

    private val _appVersion = NotNullMutableLiveData("1.0")
    val appVersion: NotNullLiveData<String> = _appVersion

    fun fetchMember() {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }
            when (val result = memberRepository.getMember(token.uid)) {
                is ApiError, is ApiException ->
                    _member.value = _member.value.changeToFetchingErrorState()

                is ApiSuccess ->
                    _member.value =
                        _member.value.changeMemberState(result.data)
            }
        }
    }

    fun deleteMember() {
        _member.value = _member.value.changeToLoadingState()
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }
            when (memberRepository.deleteMember(token.uid)) {
                is ApiError, is ApiException ->
                    _member.value = _member.value.changeToDeleteErrorState()

                is ApiSuccess ->
                    _member.value = _member.value.changeToDeletedState()
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
