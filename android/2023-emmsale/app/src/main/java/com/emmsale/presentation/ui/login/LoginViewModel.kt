package com.emmsale.presentation.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.emmsale.data.repository.interfaces.ConfigRepository
import com.emmsale.data.repository.interfaces.FcmTokenRepository
import com.emmsale.data.repository.interfaces.LoginRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.model.Login
import com.emmsale.model.Token
import com.emmsale.presentation.base.NetworkViewModel
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import com.emmsale.presentation.ui.login.uiState.LoginUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val tokenRepository: TokenRepository,
    private val fcmTokenRepository: FcmTokenRepository,
    private val configRepository: ConfigRepository,
) : NetworkViewModel() {

    private val _uiEvent = SingleLiveEvent<LoginUiEvent>()
    val uiEvent: LiveData<LoginUiEvent> = _uiEvent

    fun login(fcmToken: String, code: String): Job = command(
        command = { loginRepository.saveGithubCode(code) },
        onSuccess = {
            saveTokens(it.token, fcmToken)
            handleLoginComplete(it)
        },
        onFailure = { _, _ -> _uiEvent.value = LoginUiEvent.LoginFail },
        onLoading = { changeToLoadingState() },
    )

    private suspend fun saveTokens(token: Token, fcmToken: String) {
        viewModelScope.launch((Dispatchers.Default)) {
            fcmTokenRepository.saveFcmToken(token.uid, fcmToken)
            tokenRepository.saveToken(token)
        }
    }

    private fun handleLoginComplete(login: Login) {
        if (login.isDoneOnboarding) {
            _uiEvent.value = LoginUiEvent.LoginComplete
            configRepository.saveAutoLoginConfig(true)
        } else {
            _uiEvent.value = LoginUiEvent.JoinComplete
        }
    }
}
