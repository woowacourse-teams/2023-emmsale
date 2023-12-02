package com.emmsale.presentation.ui.myProfile

import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.base.NetworkViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.myProfile.uiState.MyProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
) : NetworkViewModel() {

    private val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _myProfile = NotNullMutableLiveData(MyProfileUiState())
    val myProfile: NotNullLiveData<MyProfileUiState> = _myProfile

    init {
        fetchProfile()
    }

    private fun fetchProfile(): Job = fetchData(
        fetchData = { memberRepository.getMember(uid) },
        onSuccess = { _myProfile.value = _myProfile.value.changeMemberState(it) },
    )

    override fun refresh(): Job = refreshData(
        refresh = { memberRepository.getMember(uid) },
        onSuccess = { _myProfile.value = _myProfile.value.changeMemberState(it) },
    )
}
