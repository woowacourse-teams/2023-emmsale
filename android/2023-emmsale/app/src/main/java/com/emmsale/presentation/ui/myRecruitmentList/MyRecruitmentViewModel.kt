package com.emmsale.presentation.ui.myRecruitmentList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.RecruitmentRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.myRecruitmentList.uiState.MyRecruitmentsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyRecruitmentViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val recruitmentRepository: RecruitmentRepository,
) : ViewModel(), Refreshable {
    private val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _myRecruitments: NotNullMutableLiveData<MyRecruitmentsUiState> =
        NotNullMutableLiveData(MyRecruitmentsUiState())
    val myRecruitments: NotNullLiveData<MyRecruitmentsUiState> = _myRecruitments

    init {
        refresh()
    }

    override fun refresh() {
        _myRecruitments.value = _myRecruitments.value.copy(isLoading = true)
        viewModelScope.launch {
            when (val result = recruitmentRepository.getMemberRecruitments(uid)) {
                is Failure, NetworkError ->
                    _myRecruitments.value =
                        _myRecruitments.value.copy(isLoading = false, isError = true)

                is Success -> _myRecruitments.value = MyRecruitmentsUiState.from(result.data)
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }
}
