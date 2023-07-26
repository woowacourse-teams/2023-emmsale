package com.emmsale.presentation.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.fcmToken.FcmToken
import com.emmsale.data.fcmToken.FcmTokenRepository
import com.emmsale.data.login.Login
import com.emmsale.data.login.LoginRepository
import com.emmsale.data.token.Token
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

    fun login(fcmToken: String) {
        changeLoginState(LoginUiState.Loading)

        viewModelScope.launch {
            when (val loginResult = loginRepository.login()) {
                is ApiSuccess -> saveTokens(loginResult.data, fcmToken)
                is ApiError -> changeLoginState(LoginUiState.Error)
                is ApiException -> changeLoginState(LoginUiState.Error)
            }
        }
    }

    private suspend fun saveTokens(login: Login, fcmToken: String) {
        saveUserToken(login)
        saveFcmToken(login.uid, fcmToken)
        when (login.isRegistered) {
            true -> changeLoginState(LoginUiState.Register)
            false -> changeLoginState(LoginUiState.Login)
        }
    }

    private suspend fun saveFcmToken(uid: Long, fcmToken: String): Job = viewModelScope.launch {
        fcmTokenRepository.saveFcmToken(FcmToken(uid, fcmToken))
    }

    private suspend fun saveUserToken(loginResult: Login) {
        tokenRepository.saveToken(Token.from(loginResult))
    }

    private fun changeLoginState(loginState: LoginUiState) {
        _loginState.postValue(loginState)
    }

    companion object {
        val factory = ViewModelFactory {
            val repositoryContainer = KerdyApplication.repositoryContainer
            LoginViewModel(
                loginRepository = repositoryContainer.loginRepository,
                tokenRepository = repositoryContainer.tokenRepository,
                fcmTokenRepository = repositoryContainer.fcmTokenRepository,
            )
        }
    }
}
