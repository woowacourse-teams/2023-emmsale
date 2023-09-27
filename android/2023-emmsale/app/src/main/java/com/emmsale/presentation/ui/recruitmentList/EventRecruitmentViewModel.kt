package com.emmsale.presentation.ui.recruitmentList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.model.RecruitmentPost
import com.emmsale.data.repository.interfaces.RecruitmentRepository
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.recruitmentList.uiState.RecruitmentPostsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventRecruitmentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recruitmentRepository: RecruitmentRepository,
) : ViewModel(), Refreshable {
    val eventId: Long = requireNotNull(savedStateHandle[EVENT_ID_KEY]) {
        "[ERROR] 행사 아이디를 가져오지 못했어요"
    }

    private val _recruitments: NotNullMutableLiveData<RecruitmentPostsUiState> =
        NotNullMutableLiveData(
            RecruitmentPostsUiState(),
        )
    val recruitments: NotNullLiveData<RecruitmentPostsUiState> = _recruitments

    override fun refresh() {
        fetchRecruitments()
    }

    private fun fetchRecruitments() {
        changeRecruitmentsToLoadingState()
        viewModelScope.launch {
            when (val result = recruitmentRepository.getEventRecruitments(eventId)) {
                is Failure, NetworkError -> changeRecruitmentsToErrorState()
                is Success -> fetchSuccessRecruitments(result.data)
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    private fun changeRecruitmentsToLoadingState() {
        _recruitments.value = _recruitments.value.changeToLoadingState()
    }

    private fun changeRecruitmentsToErrorState() {
        _recruitments.value = _recruitments.value.changeToErrorState()
    }

    private fun fetchSuccessRecruitments(recruitmentPosts: List<RecruitmentPost>) {
        _recruitments.value = RecruitmentPostsUiState.from(recruitmentPosts)
    }

    companion object {
        const val EVENT_ID_KEY = "EVENT_ID_KEY"
    }
}
