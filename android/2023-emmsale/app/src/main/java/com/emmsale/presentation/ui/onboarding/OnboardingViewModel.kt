package com.emmsale.presentation.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.education.EducationRepository
import com.emmsale.presentation.base.viewmodel.BaseViewModel
import com.emmsale.presentation.base.viewmodel.DispatcherProvider
import com.emmsale.presentation.ui.onboarding.model.EducationUiState
import com.emmsale.presentation.ui.onboarding.uistate.EducationsUiState
import com.emmsale.presentation.utils.livedata.DistinctListLiveData

class OnboardingViewModel(
    dispatcherProvider: DispatcherProvider,
    private val educationRepository: EducationRepository,
) : BaseViewModel(dispatcherProvider) {
    val nameUiState: MutableLiveData<String> = MutableLiveData()

    private val _educations: MutableLiveData<EducationsUiState> = MutableLiveData()
    val educations: LiveData<EducationsUiState> = _educations
    private val loadedEducations = mutableListOf<EducationUiState>()

    private val _selectedEducations: DistinctListLiveData<EducationUiState> = DistinctListLiveData()
    val selectedEducations: LiveData<List<EducationUiState>> = _selectedEducations.asLiveData()

    init {
        fetchEducations()
    }

    private fun fetchEducations() {
        onIo {
            when (val result = educationRepository.fetchEducations()) {
                is ApiSuccess -> {
                    val educationsUiState = EducationsUiState.from(result)
                    _educations.postValue(educationsUiState.insertFront(EducationUiState.empty()))
                    loadedEducations.clear()
                    loadedEducations.addAll(EducationUiState.empty() + educationsUiState.educations)
                }

                is ApiError -> _educations.postValue(EducationsUiState.Error)
                is ApiException -> _educations.postValue(EducationsUiState.Error)
            }
        }
    }

    fun addEduHistory(position: Int) {
        _selectedEducations.add(loadedEducations[position])
    }

    fun removeEduHistory(education: EducationUiState) {
        _selectedEducations.remove(education)
    }
}
