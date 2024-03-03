package com.emmsale.presentation.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.emmsale.BuildConfig
import com.emmsale.model.Member
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import com.emmsale.presentation.ui.setting.uiState.SettingUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
) : RefreshableViewModel() {

    private val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _member = NotNullMutableLiveData(Member())
    val member: NotNullLiveData<Member> = _member

    val appVersion: String = BuildConfig.VERSION_NAME

    private val _uiEvent = SingleLiveEvent<SettingUiEvent>()
    val uiEvent: LiveData<SettingUiEvent> = _uiEvent

    init {
        fetchMember()
    }

    private fun fetchMember(): Job = fetchData(
        fetchData = { memberRepository.getMember(uid) },
        onSuccess = { _member.value = it },
    )

    override fun refresh(): Job = refreshData(
        refresh = { memberRepository.getMember(uid) },
        onSuccess = { _member.value = it },
    )

    fun logout(): Job = viewModelScope.launch {
        tokenRepository.deleteToken()
        _uiEvent.value = SettingUiEvent.Logout
    }
}
