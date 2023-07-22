package com.emmsale.presentation.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.emmsale.data.career.CareerRepository
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.presentation.base.viewmodel.BaseViewModel
import com.emmsale.presentation.base.viewmodel.DispatcherProvider
import com.emmsale.presentation.ui.onboarding.uistate.CareerContentUiState
import com.emmsale.presentation.ui.onboarding.uistate.CareerUiState
import com.emmsale.presentation.ui.onboarding.uistate.CareersUiState

class OnboardingViewModel(
    dispatcherProvider: DispatcherProvider,
    private val careerRepository: CareerRepository,
) : BaseViewModel(dispatcherProvider) {
    val nameUiState: MutableLiveData<String> = MutableLiveData()

    // TODO("Error Handling on OnboardingActivity")
    private val _careers: MutableLiveData<CareersUiState> = MutableLiveData()
    private val selectedCareerIds: MutableList<Int> = mutableListOf()

    val educations: LiveData<CareerUiState?> = _careers.map { careers ->
        when (careers) {
            is CareersUiState.Success -> careers.findCareer(CareerCategory.EDUCATION)
            is CareersUiState.Error -> null
        }
    }

    val clubs: LiveData<CareerUiState?> = _careers.map { careers ->
        when (careers) {
            is CareersUiState.Success -> careers.findCareer(CareerCategory.CLUB)
            is CareersUiState.Error -> null
        }
    }

    val jobs: LiveData<CareerUiState?> = _careers.map { careers ->
        when (careers) {
            is CareersUiState.Success -> careers.findCareer(CareerCategory.JOB)
            is CareersUiState.Error -> null
        }
    }

    init {
        fetchCareers()
    }

    private fun fetchCareers() {
        onIo {
            when (val careersApiModel = careerRepository.getCareers()) {
                is ApiSuccess -> _careers.postValue(CareersUiState.from(careersApiModel.data))
                is ApiError -> _careers.postValue(CareersUiState.Error)
                is ApiException -> _careers.postValue(CareersUiState.Error)
            }
        }
    }

    fun toggleTagSelection(tag: CareerContentUiState) {
        tag.isSelected = !tag.isSelected
        when {
            tag.isSelected -> selectedCareerIds.add(tag.id)
            else -> selectedCareerIds.remove(tag.id)
        }
    }
}
