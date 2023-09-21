package com.emmsale.presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.repository.interfaces.ConfigRepository
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.splash.uiState.SplashUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
) : ViewModel() {
    private val _splash = NotNullMutableLiveData<SplashUiState>(SplashUiState.Loading())
    val splash: NotNullLiveData<SplashUiState> = _splash

    init {
        autoLogin()
    }

    private fun autoLogin() {
        viewModelScope.launch {
            val autoLogin = configRepository.getConfig().isAutoLogin
            val splashState = splash.value

            if (splashState is SplashUiState.Loading) {
                delay(splashState.splashTimeMs)
            }

            _splash.value = SplashUiState.Done(isAutoLogin = autoLogin)
        }
    }
}
