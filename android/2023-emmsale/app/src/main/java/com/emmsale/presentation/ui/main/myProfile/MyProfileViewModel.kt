package com.emmsale.presentation.ui.main.myProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.member.MemberRepository
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.ui.main.myProfile.uiState.MyProfileScreenUiState
import kotlinx.coroutines.launch

class MyProfileViewModel(
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData(MyProfileScreenUiState.Loading)
    val uiState: LiveData<MyProfileScreenUiState> = _uiState

    fun fetchMember() {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
                ?: throw IllegalStateException("로그인을 하지 않은 상태로 이 화면에 올 수 없습니다. 로그인 로직을 다시 살펴보세요.")
            when (val result = memberRepository.fetchMember(token.uid)) {
                is ApiError -> changeErrorUiState(result.message.toString())
                is ApiException -> changeErrorUiState(result.e.message.toString())
                is ApiSuccess -> _uiState.postValue(MyProfileScreenUiState.from(result.data))
            }
        }
    }

    private fun changeErrorUiState(errorMessage: String) {
        _uiState.postValue(
            uiState.value!!.copy(
                isLoading = false,
                isError = true,
                errorMessage = errorMessage
            )
        )
    }

    companion object {
        val factory = ViewModelFactory {
            MyProfileViewModel(
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                memberRepository = KerdyApplication.repositoryContainer.memberRepository
            )
        }
    }
}
