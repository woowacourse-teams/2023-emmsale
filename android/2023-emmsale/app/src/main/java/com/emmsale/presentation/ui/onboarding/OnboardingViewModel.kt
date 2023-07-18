package com.emmsale.presentation.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emmsale.presentation.base.viewmodel.BaseViewModel
import com.emmsale.presentation.base.viewmodel.DispatcherProvider

class OnboardingViewModel(
    dispatcherProvider: DispatcherProvider,
) : BaseViewModel(dispatcherProvider) {
    private val _name: MutableLiveData<String> = MutableLiveData()
    val name: LiveData<String> = _name
}
