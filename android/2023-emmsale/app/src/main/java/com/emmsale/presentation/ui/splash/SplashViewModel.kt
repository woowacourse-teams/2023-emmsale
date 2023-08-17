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
    private val _splashState = NotNullMutableLiveData<SplashUiState>(SplashUiState.Loading())
    val splashState: NotNullLiveData<SplashUiState> = _splashState

    init {
        autoLogin()
    }

    private fun autoLogin() {
        viewModelScope.launch {
            val autoLogin = configRepository.getConfig().isAutoLogin
            delay(1000)

            _splashState.value = SplashUiState.Done(isAutoLogin = autoLogin)
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
