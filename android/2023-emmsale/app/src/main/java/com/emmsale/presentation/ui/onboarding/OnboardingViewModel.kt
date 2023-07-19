package com.emmsale.presentation.ui.onboarding

import androidx.lifecycle.MutableLiveData
import com.emmsale.presentation.base.viewmodel.BaseViewModel
import com.emmsale.presentation.base.viewmodel.DispatcherProvider

class OnboardingViewModel(
    dispatcherProvider: DispatcherProvider,
) : BaseViewModel(dispatcherProvider) {
    val name: MutableLiveData<String> = MutableLiveData()
}
