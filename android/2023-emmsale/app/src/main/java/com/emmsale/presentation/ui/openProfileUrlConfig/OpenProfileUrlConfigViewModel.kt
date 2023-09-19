package com.emmsale.presentation.ui.openProfileUrlConfig

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.TokenRepository
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
            when (val result = memberRepository.getMember(myUid)) {
                is Failure, NetworkError -> _isUrlFetchError.value = true
                is Success -> {
                    _isUrlFetchError.value = false
                    url.value = result.data.openProfileUrl
                }

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun updateOpenProfileUrl(url: String) {
        viewModelScope.launch {
            when (val result = memberRepository.updateMemberOpenProfileUrl(url)) {
                is Failure, NetworkError -> _isUpdateUrlSuccess.value = false
                is Success -> _isUpdateUrlSuccess.value = true
                is Unexpected -> throw Throwable(result.error)
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
