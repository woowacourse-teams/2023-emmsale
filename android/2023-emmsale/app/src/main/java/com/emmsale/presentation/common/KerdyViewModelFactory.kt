package com.emmsale.presentation.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emmsale.data.career.CareerRepositoryImpl
import com.emmsale.data.common.RetrofitProvider
import com.emmsale.data.login.LoginRepositoryImpl
import com.emmsale.data.token.TokenRepositoryImpl
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.base.viewmodel.DispatcherProviderImpl
import com.emmsale.presentation.common.keys.KERDY_PREF_KEY
import com.emmsale.presentation.ui.login.LoginViewModel
import com.emmsale.presentation.ui.onboarding.OnboardingViewModel

val viewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = with(modelClass) {
        when {
            isAssignableFrom(LoginViewModel::class.java) -> createLoginViewModel()
            isAssignableFrom(OnboardingViewModel::class.java) -> createOnboardingViewModel()
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    } as T
}

private fun createLoginViewModel(): LoginViewModel {
    val kerdyPreference =
        KerdyApplication.context.getSharedPreferences(KERDY_PREF_KEY, Context.MODE_PRIVATE)

    return LoginViewModel(
        dispatcherProvider = DispatcherProviderImpl(),
        loginRepository = LoginRepositoryImpl(RetrofitProvider.loginService),
        tokenRepository = TokenRepositoryImpl(preference = kerdyPreference)
    )
}

private fun createOnboardingViewModel(): OnboardingViewModel = OnboardingViewModel(
    dispatcherProvider = DispatcherProviderImpl(),
    careerRepository = CareerRepositoryImpl(RetrofitProvider.careerService),
)
