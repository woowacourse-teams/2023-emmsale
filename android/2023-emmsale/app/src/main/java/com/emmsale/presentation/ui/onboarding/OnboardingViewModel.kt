package com.emmsale.presentation.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.emmsale.data.career.CareerRepository
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.ui.onboarding.uistate.CareerContentUiState
import com.emmsale.presentation.ui.onboarding.uistate.CareerUiState
import com.emmsale.presentation.ui.onboarding.uistate.CareersUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val careerRepository: CareerRepository,
) : ViewModel() {
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

    private fun fetchCareers(): Job = viewModelScope.launch {
        when (val careersApiModel = careerRepository.getCareers()) {
            is ApiSuccess -> _careers.postValue(CareersUiState.from(careersApiModel.data))
            is ApiError -> _careers.postValue(CareersUiState.Error)
            is ApiException -> _careers.postValue(CareersUiState.Error)
        }
    }

    fun toggleTagSelection(tag: CareerContentUiState) {
        tag.isSelected = !tag.isSelected
        when (tag.isSelected) {
            true -> selectedCareerIds.add(tag.id)
            false -> selectedCareerIds.remove(tag.id)
        }
    }

    companion object {
        val factory = ViewModelFactory {
            OnboardingViewModel(KerdyApplication.repositoryContainer.careerRepository)
        }
    }
}
