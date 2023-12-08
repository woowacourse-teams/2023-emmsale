package com.emmsale.presentation.ui.recruitmentList

import androidx.lifecycle.SavedStateHandle
import com.emmsale.data.model.Recruitment
import com.emmsale.data.repository.interfaces.RecruitmentRepository
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class RecruitmentsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recruitmentRepository: RecruitmentRepository,
) : RefreshableViewModel() {
    val eventId: Long = requireNotNull(savedStateHandle[EVENT_ID_KEY]) {
        "[ERROR] 행사 아이디를 가져오지 못했어요"
    }

    private val _recruitments = NotNullMutableLiveData(listOf<Recruitment>())
    val recruitments: NotNullLiveData<List<Recruitment>> = _recruitments

    init {
        fetchRecruitments()
    }

    private fun fetchRecruitments(): Job = fetchData(
        fetchData = { recruitmentRepository.getEventRecruitments(eventId) },
        onSuccess = { _recruitments.value = it },
    )

    override fun refresh(): Job = refreshData(
        refresh = { recruitmentRepository.getEventRecruitments(eventId) },
        onSuccess = { _recruitments.value = it },
    )

    companion object {
        const val EVENT_ID_KEY = "EVENT_ID_KEY"
    }
}
