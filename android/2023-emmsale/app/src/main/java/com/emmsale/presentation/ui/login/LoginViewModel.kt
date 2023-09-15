package com.emmsale.presentation.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.model.Login
import com.emmsale.data.repository.interfaces.ConfigRepository
import com.emmsale.data.repository.interfaces.FcmTokenRepository
import com.emmsale.data.repository.interfaces.LoginRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.login.uiState.LoginUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val tokenRepository: TokenRepository,
    private val fcmTokenRepository: FcmTokenRepository,
    private val configRepository: ConfigRepository,
) : ViewModel() {
    private val _loginState: MutableLiveData<LoginUiState> = MutableLiveData()
    val loginState: LiveData<LoginUiState> = _loginState

    fun login(fcmToken: String, code: String) {
        changeLoginState(LoginUiState.Loading)

        viewModelScope.launch {
            when (val result = loginRepository.saveGithubCode(code)) {
                is Failure, NetworkError -> changeLoginState(LoginUiState.Error)
                is Success -> saveTokens(result.data, fcmToken)
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    private suspend fun saveTokens(login: Login, fcmToken: String) {
        joinAll(saveUserToken(login), saveFcmToken(login.token.uid, fcmToken))

        if (login.isDoneOnboarding) {
            changeLoginState(LoginUiState.Login)
            configRepository.saveAutoLoginConfig(true)
        } else {
            changeLoginState(LoginUiState.Onboarded)
        }
    }

    private fun saveFcmToken(uid: Long, fcmToken: String): Job = viewModelScope.launch {
        fcmTokenRepository.saveFcmToken(uid, fcmToken)
    }

    private fun saveUserToken(login: Login): Job = viewModelScope.launch {
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
                configRepository = KerdyApplication.repositoryContainer.configRepository,
            )
        }
    }
}
