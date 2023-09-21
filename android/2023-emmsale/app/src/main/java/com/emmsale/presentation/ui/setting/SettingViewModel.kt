package com.emmsale.presentation.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.BuildConfig
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.setting.uiState.MemberUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
) : ViewModel(), Refreshable {

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _member = NotNullMutableLiveData(MemberUiState.FIRST_LOADING)
    val member: NotNullLiveData<MemberUiState> = _member

    private val _appVersion = NotNullMutableLiveData(BuildConfig.VERSION_NAME)
    val appVersion: NotNullLiveData<String> = _appVersion

    init {
        refresh()
    }

    override fun refresh() {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }
            when (val result = memberRepository.getMember(token.uid)) {
                is Failure, NetworkError -> _member.value = _member.value.changeToErrorState()
                is Success -> _member.value = _member.value.changeMemberState(result.data)
                is Unexpected -> throw Throwable(result.error)
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
}
