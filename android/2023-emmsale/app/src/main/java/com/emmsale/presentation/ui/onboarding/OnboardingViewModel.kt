package com.emmsale.presentation.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.resumeTag.ResumeTagRepository
import com.emmsale.presentation.base.viewmodel.BaseViewModel
import com.emmsale.presentation.base.viewmodel.DispatcherProvider
import com.emmsale.presentation.ui.onboarding.uistate.ResumeTagUiState
import com.emmsale.presentation.ui.onboarding.uistate.ResumeTagsUiState
import com.emmsale.presentation.utils.livedata.DistinctListLiveData

class OnboardingViewModel(
    dispatcherProvider: DispatcherProvider,
    private val resumeTagRepository: ResumeTagRepository,
) : BaseViewModel(dispatcherProvider) {
    val nameUiState: MutableLiveData<String> = MutableLiveData()

    private val _educationTags: MutableLiveData<ResumeTagsUiState> = MutableLiveData()
    val educationTags: LiveData<ResumeTagsUiState> = _educationTags
    private val loadedEducationTags = mutableListOf<ResumeTagUiState>()

    private val _selectedEducationTags: DistinctListLiveData<ResumeTagUiState> =
        DistinctListLiveData()
    val selectedEducationTags: LiveData<List<ResumeTagUiState>> =
        _selectedEducationTags.asLiveData()

    private val _conferenceTags: MutableLiveData<ResumeTagsUiState> = MutableLiveData()
    val conferenceTags: LiveData<ResumeTagsUiState> = _conferenceTags
    private val loadedConferenceTags = mutableListOf<ResumeTagUiState>()

    private val _selectedConferenceTags: DistinctListLiveData<ResumeTagUiState> =
        DistinctListLiveData()
    val selectedConferenceTags: LiveData<List<ResumeTagUiState>> =
        _selectedConferenceTags.asLiveData()

    init {
        fetchResumeTags()
    }

    private fun fetchResumeTags() {
        fetchEducationTags()
        fetchConferenceTags()
    }

    private fun fetchEducationTags() {
        onIo {
            when (val educationTagsResult = resumeTagRepository.getEducationTags()) {
                is ApiSuccess -> {
                    val educationTagsUiState = ResumeTagsUiState.from(educationTagsResult)
                    _educationTags.postValue(educationTagsUiState.insertFront(ResumeTagUiState.empty()))
                    loadedEducationTags.clear()
                    loadedEducationTags.addAll(ResumeTagUiState.empty() + educationTagsUiState.tags)
                }

                is ApiError -> _educationTags.postValue(ResumeTagsUiState.Error)
                is ApiException -> _educationTags.postValue(ResumeTagsUiState.Error)
            }
        }
    }

    private fun fetchConferenceTags() {
        onIo {
            when (val conferenceTagsResult = resumeTagRepository.getConferenceTags()) {
                is ApiSuccess -> {
                    val conferenceTagsUiState = ResumeTagsUiState.from(conferenceTagsResult)
                    _conferenceTags.postValue(conferenceTagsUiState.insertFront(ResumeTagUiState.empty()))
                    loadedConferenceTags.clear()
                    loadedConferenceTags.addAll(ResumeTagUiState.empty() + conferenceTagsUiState.tags)
                }

                is ApiError -> _conferenceTags.postValue(ResumeTagsUiState.Error)
                is ApiException -> _conferenceTags.postValue(ResumeTagsUiState.Error)
            }
        }
    }

    fun addEducationTag(position: Int) {
        _selectedEducationTags.add(loadedEducationTags[position])
    }

    fun removeEducationTag(educationTag: ResumeTagUiState) {
        _selectedEducationTags.remove(educationTag)
    }

    fun addConferenceTag(position: Int) {
        _selectedConferenceTags.add(loadedConferenceTags[position])
    }

    fun removeConferenceTag(conferenceTag: ResumeTagUiState) {
        _selectedConferenceTags.remove(conferenceTag)
    }
}
