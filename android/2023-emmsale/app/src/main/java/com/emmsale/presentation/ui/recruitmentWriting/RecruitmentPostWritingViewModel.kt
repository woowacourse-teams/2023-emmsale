package com.emmsale.presentation.ui.recruitmentWriting

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.repository.interfaces.RecruitmentRepository
import com.emmsale.presentation.base.NetworkViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import com.emmsale.presentation.ui.recruitmentList.uiState.WritingModeUiState
import com.emmsale.presentation.ui.recruitmentWriting.uiState.RecruitmentPostWritingUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class RecruitmentPostWritingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recruitmentRepository: RecruitmentRepository,
) : NetworkViewModel() {
    val eventId: Long = savedStateHandle[EVENT_ID_KEY] ?: DEFAULT_EVENT_ID
    private val recruitmentIdToEdit: Long? = savedStateHandle[RECRUITMENT_ID_KEY]
    val recruitmentContentToEdit: String =
        savedStateHandle[RECRUITMENT_CONTENT_KEY] ?: DEFAULT_CONTENT

    val writingMode: WritingModeUiState =
        if (recruitmentIdToEdit == null) WritingModeUiState.POST else WritingModeUiState.EDIT

    private val _canPost = NotNullMutableLiveData(true)
    val canPost: NotNullLiveData<Boolean> = _canPost

    private val _uiEvent = SingleLiveEvent<RecruitmentPostWritingUiEvent>()
    val uiEvent: LiveData<RecruitmentPostWritingUiEvent> = _uiEvent

    fun postRecruitment(content: String): Job = command(
        command = { recruitmentRepository.postRecruitment(eventId, content) },
        onSuccess = { _uiEvent.value = RecruitmentPostWritingUiEvent.PostComplete(it) },
        onFailure = { _, _ -> _uiEvent.value = RecruitmentPostWritingUiEvent.PostFail },
        onStart = { _canPost.value = false },
        onFinish = { _canPost.value = true },
    )

    fun editRecruitment(content: String): Job = command(
        command = {
            if (recruitmentIdToEdit == null) return@command Failure(-1, "")
            recruitmentRepository.editRecruitment(eventId, recruitmentIdToEdit, content)
        },
        onSuccess = { _uiEvent.value = RecruitmentPostWritingUiEvent.EditComplete },
        onFailure = { _, _ -> _uiEvent.value = RecruitmentPostWritingUiEvent.EditFail },
        onStart = { _canPost.value = false },
        onFinish = { _canPost.value = true },
    )

    companion object {
        const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val DEFAULT_EVENT_ID = -1L

        const val RECRUITMENT_ID_KEY = "RECRUITMENT_ID_KEY"

        const val RECRUITMENT_CONTENT_KEY = "RECRUITMENT_CONTENT_KEY"
        private const val DEFAULT_CONTENT = ""
    }
}
