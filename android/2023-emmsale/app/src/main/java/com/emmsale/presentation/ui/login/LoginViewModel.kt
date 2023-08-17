package com.emmsale.presentation.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.fcmToken.FcmTokenRepository
import com.emmsale.data.login.Login
import com.emmsale.data.login.LoginRepository
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.ui.login.uistate.LoginUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val tokenRepository: TokenRepository,
    private val fcmTokenRepository: FcmTokenRepository,
) : ViewModel() {
    private val _loginState: MutableLiveData<LoginUiState> = MutableLiveData()
    val loginState: LiveData<LoginUiState> = _loginState

    init {
        viewModelScope.launch {
            val isAutoLogin = tokenRepository.getToken()?.isAutoLogin ?: return@launch
            if (isAutoLogin) changeLoginState(LoginUiState.Login)
        }
    }

    fun login(fcmToken: String, code: String) {
        changeLoginState(LoginUiState.Loading)

        viewModelScope.launch {
            when (val loginResult = loginRepository.saveGithubCode(code)) {
                is ApiSuccess -> saveTokens(loginResult.data, fcmToken)
                is ApiError -> changeLoginState(LoginUiState.Error)
                is ApiException -> changeLoginState(LoginUiState.Error)
            }
        }
    }

    private suspend fun saveTokens(login: Login, fcmToken: String) {
        saveUserToken(login)
        saveFcmToken(login.token.uid, fcmToken)
        when (login.isOnboarded) {
            true -> changeLoginState(LoginUiState.Login)
            false -> changeLoginState(LoginUiState.Onboarded)
        }
    }

    private suspend fun saveFcmToken(uid: Long, fcmToken: String): Job = viewModelScope.launch {
        fcmTokenRepository.saveFcmToken(uid, fcmToken)
    }

    private suspend fun saveUserToken(login: Login) {
        tokenRepository.saveToken(login.token)
    }

    private fun changeLoginState(loginState: LoginUiState) {
        _loginState.postValue(loginState)
    }

    companion object {
        val factory = ViewModelFactory {
            LoginViewModel(
                loginRepository = KerdyApplication.repositoryContainer.loginRepository,
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                fcmTokenRepository = KerdyApplication.repositoryContainer.fcmTokenRepository,
            )
        }
    }
}
