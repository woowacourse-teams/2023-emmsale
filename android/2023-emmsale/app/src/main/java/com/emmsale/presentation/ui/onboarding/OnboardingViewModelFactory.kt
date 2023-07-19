package com.emmsale.presentation.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emmsale.data.education.EducationRepositoryImpl
import com.emmsale.presentation.base.viewmodel.DispatcherProviderImpl

class OnboardingViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
            return OnboardingViewModel(
                dispatcherProvider = DispatcherProviderImpl(),
                educationRepository = EducationRepositoryImpl(),
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
