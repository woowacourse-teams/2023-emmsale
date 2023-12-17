package com.emmsale.presentation.ui.myRecruitmentList

import com.emmsale.data.model.Recruitment
import com.emmsale.data.repository.interfaces.RecruitmentRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class MyRecruitmentViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val recruitmentRepository: RecruitmentRepository,
) : RefreshableViewModel() {
    private val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _myRecruitments: NotNullMutableLiveData<List<Recruitment>> =
        NotNullMutableLiveData(emptyList())
    val myRecruitments: NotNullLiveData<List<Recruitment>> = _myRecruitments

    init {
        fetchMyRecruitments()
    }

    private fun fetchMyRecruitments() {
        fetchData(
            fetchData = { recruitmentRepository.getMemberRecruitments(uid) },
            onSuccess = { _myRecruitments.value = it },
        )
    }

    override fun refresh(): Job = refreshData(
        refresh = { recruitmentRepository.getMemberRecruitments(uid) },
        onSuccess = { _myRecruitments.value = it },
    )
}
