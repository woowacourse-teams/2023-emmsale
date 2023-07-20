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

    private val _selectedEducationTags: DistinctListLiveData<ResumeTagUiState> = DistinctListLiveData()
    val selectedEducationTags: LiveData<List<ResumeTagUiState>> = _selectedEducationTags.asLiveData()

    init {
        fetchEducationTags()
    }

    private fun fetchEducationTags() {
        onIo {
            when (val result = resumeTagRepository.getEducationTags()) {
                is ApiSuccess -> {
                    val resumeTagsUiState = ResumeTagsUiState.from(result)
                    _educationTags.postValue(resumeTagsUiState.insertFront(ResumeTagUiState.empty()))
                    loadedEducationTags.clear()
                    loadedEducationTags.addAll(ResumeTagUiState.empty() + resumeTagsUiState.tags)
                }

                is ApiError -> _educationTags.postValue(ResumeTagsUiState.Error)
                is ApiException -> _educationTags.postValue(ResumeTagsUiState.Error)
            }
        }
    }

    fun addEducationTag(position: Int) {
        _selectedEducationTags.add(loadedEducationTags[position])
    }

    fun removeEducationTag(educationTag: ResumeTagUiState) {
        _selectedEducationTags.remove(educationTag)
    }
}
