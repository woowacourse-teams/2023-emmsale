package com.emmsale.presentation.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emmsale.presentation.base.viewmodel.DispatcherProviderImpl

class OnboardingViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
            return OnboardingViewModel(DispatcherProviderImpl()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
