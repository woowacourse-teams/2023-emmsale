package com.emmsale.presentation.ui.myProfile

import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.model.Member
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
) : RefreshableViewModel() {

    private val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _profile = NotNullMutableLiveData(Member())
    val profile: NotNullLiveData<Member> = _profile

    init {
        fetchProfile()
    }

    private fun fetchProfile(): Job = fetchData(
        fetchData = { memberRepository.getMember(uid) },
        onSuccess = { _profile.value = it },
    )

    override fun refresh(): Job = refreshData(
        refresh = { memberRepository.getMember(uid) },
        onSuccess = { _profile.value = it },
    )
}
