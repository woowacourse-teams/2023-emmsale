package com.emmsale.presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.config.ConfigRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.splash.uistate.SplashUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
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

    companion object {
        val factory = ViewModelFactory {
            SplashViewModel(
                configRepository = KerdyApplication.repositoryContainer.configRepository,
            )
        }
    }
}
