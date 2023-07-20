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
import com.emmsale.presentation.common.livedata.DistinctListLiveData
import com.emmsale.presentation.ui.onboarding.uistate.CareerContentUiState
import com.emmsale.presentation.ui.onboarding.uistate.CareerUiState
import com.emmsale.presentation.ui.onboarding.uistate.CareersUiState

class OnboardingViewModel(
    dispatcherProvider: DispatcherProvider,
    private val careerRepository: CareerRepository,
) : BaseViewModel(dispatcherProvider) {
    val nameUiState: MutableLiveData<String> = MutableLiveData()

    private val _careers: MutableLiveData<CareersUiState> = MutableLiveData()

    val educations: LiveData<CareerUiState?> = _careers.map { careers ->
        when (careers) {
            is CareersUiState.Success -> careers.findCareer(CareerCategory.EDUCATION)
            is CareersUiState.Error -> null
        }
    }

    private val selectedCareerIds: MutableList<Int> = mutableListOf()

    private val _selectedEducationTags: DistinctListLiveData<CareerContentUiState> =
        DistinctListLiveData()
    val selectedEducationTags: LiveData<List<CareerContentUiState>> =
        _selectedEducationTags.asLiveData()

    val conferences: LiveData<CareerUiState?> = _careers.map { careers ->
        when (careers) {
            is CareersUiState.Success -> careers.findCareer(CareerCategory.CONFERENCE)
            is CareersUiState.Error -> null
        }
    }
    private val _selectedConferenceTags: DistinctListLiveData<CareerContentUiState> =
        DistinctListLiveData()
    val selectedConferenceTags: LiveData<List<CareerContentUiState>> =
        _selectedConferenceTags.asLiveData()

    val clubs: LiveData<CareerUiState?> = _careers.map { careers ->
        when (careers) {
            is CareersUiState.Success -> careers.findCareer(CareerCategory.CLUB)
            is CareersUiState.Error -> null
        }
    }
    private val _selectedClubTags: DistinctListLiveData<CareerContentUiState> =
        DistinctListLiveData()
    val selectedClubTags: LiveData<List<CareerContentUiState>> =
        _selectedClubTags.asLiveData()

    val jobs: LiveData<CareerUiState?> = _careers.map { careers ->
        when (careers) {
            is CareersUiState.Success -> careers.findCareer(CareerCategory.JOB)
            is CareersUiState.Error -> null
        }
    }
    private val _selectedJobTags: DistinctListLiveData<CareerContentUiState> =
        DistinctListLiveData()
    val selectedJobTags: LiveData<List<CareerContentUiState>> =
        _selectedJobTags.asLiveData()

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

    fun addEducationTag(position: Int) {
        val selectedCareer = educations.value?.careerContentsWithCategory?.get(position) ?: return
        _selectedEducationTags.add(selectedCareer)
        selectedCareerIds.add(selectedCareer.id)
    }

    fun removeEducationTag(educationTag: CareerContentUiState) {
        _selectedEducationTags.remove(educationTag)
        selectedCareerIds.remove(educationTag.id)
    }

    fun addConferenceTag(position: Int) {
        val selectedCareer = conferences.value?.careerContentsWithCategory?.get(position) ?: return
        _selectedConferenceTags.add(selectedCareer)
        selectedCareerIds.add(selectedCareer.id)
    }

    fun removeConferenceTag(conferenceTag: CareerContentUiState) {
        _selectedConferenceTags.remove(conferenceTag)
        selectedCareerIds.remove(conferenceTag.id)
    }

    fun addClubTag(position: Int) {
        val selectedCareer = clubs.value?.careerContentsWithCategory?.get(position) ?: return
        _selectedClubTags.add(selectedCareer)
        selectedCareerIds.add(selectedCareer.id)
    }

    fun removeClubTag(clubTag: CareerContentUiState) {
        _selectedClubTags.remove(clubTag)
        selectedCareerIds.remove(clubTag.id)
    }

    fun addJobTag(position: Int) {
        val selectedCareer = jobs.value?.careerContentsWithCategory?.get(position) ?: return
        _selectedJobTags.add(selectedCareer)
        selectedCareerIds.add(selectedCareer.id)
    }

    fun removeJobTag(jobTag: CareerContentUiState) {
        _selectedJobTags.remove(jobTag)
        selectedCareerIds.remove(jobTag.id)
    }
}
