package com.emmsale.presentation.ui.eventdetail.recruitment.writing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.recruitment.RecruitmentRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import kotlinx.coroutines.launch

class RecruitmentWritingViewModel(
    private val eventId: Long,
    private val recruitmentRepository: RecruitmentRepository,
) : ViewModel() {
    private val _isSuccessPostRecruitment: MutableLiveData<Boolean> = MutableLiveData()
    val isSuccessPostRecruitment: LiveData<Boolean> = _isSuccessPostRecruitment

    fun postRecruitment(content: String) {
        viewModelScope.launch {
            val response = recruitmentRepository.postRecruitment(eventId, content)
            if (response is ApiSuccess) {
                _isSuccessPostRecruitment.postValue(true)
            } else {
                _isSuccessPostRecruitment.postValue(false)
            }
        }
    }

    companion object {
        fun factory(eventId: Long) = ViewModelFactory {
            RecruitmentWritingViewModel(
                eventId,
                KerdyApplication.repositoryContainer.recruitmentRepository,
            )
        }
    }
}
