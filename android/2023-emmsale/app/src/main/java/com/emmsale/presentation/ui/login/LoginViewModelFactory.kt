package com.emmsale.presentation.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emmsale.data.common.RetrofitProvider
import com.emmsale.data.login.LoginRepositoryImpl
import com.emmsale.data.token.TokenRepositoryImpl
import com.emmsale.presentation.base.viewmodel.DispatcherProviderImpl
import com.emmsale.presentation.utils.keys.KERDY_PREF_KEY

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val kerdyPreference = context.getSharedPreferences(KERDY_PREF_KEY, Context.MODE_PRIVATE)

        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                DispatcherProviderImpl(),
                LoginRepositoryImpl(loginService = RetrofitProvider.loginService),
                TokenRepositoryImpl(preference = kerdyPreference)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
