package com.emmsale.presentation.ui.main.setting.openProfileUrl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.member.MemberRepository
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import kotlinx.coroutines.launch

class OpenProfileUrlConfigViewModel(
    private val memberRepository: MemberRepository,
    private val tokenRepository: TokenRepository,
) : ViewModel() {
    private val myUid = tokenRepository.getMyUid() ?: throw IllegalStateException(NOT_UID_ERROR)

    val url: MutableLiveData<String> = MutableLiveData("")

    private val _isUpdateUrlSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isUpdateUrlSuccess: LiveData<Boolean> = _isUpdateUrlSuccess

    private val _isUrlFetchError: MutableLiveData<Boolean> = MutableLiveData()
    val isUrlFetchError: LiveData<Boolean> = _isUrlFetchError

    val isUrlValid: LiveData<Boolean> = url.map { it.startsWith(URL_PREFIX) }

    init {
        fetchOpenProfileUrl()
    }

    private fun fetchOpenProfileUrl() {
        viewModelScope.launch {
            when (val response = memberRepository.getMember(myUid)) {
                is ApiSuccess -> {
                    _isUrlFetchError.value = false
                    url.value = response.data.openProfileUrl
                }

                is ApiError, is ApiException -> _isUrlFetchError.value = true
            }
        }
    }

    fun updateOpenProfileUrl(url: String) {
        viewModelScope.launch {
            when (memberRepository.updateMemberOpenProfileUrl(url)) {
                is ApiSuccess -> _isUpdateUrlSuccess.value = true
                is ApiError, is ApiException -> _isUpdateUrlSuccess.value = false
            }
        }
    }

    companion object {
        private const val NOT_UID_ERROR = "카카오 URL 설정 페이지에서현재 UID 를 찾을 수 없어요"
        private const val URL_PREFIX = "https://open.kakao.com/"

        val factory = ViewModelFactory {
            OpenProfileUrlConfigViewModel(
                memberRepository = KerdyApplication.repositoryContainer.memberRepository,
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
            )
        }
    }
}
