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
            if (token == null) {
                _uiState.postValue(_uiState.value!!.copy(isNotLogin = true))
                return@launch
            }
            when (val result = memberRepository.getMember(token.uid)) {
                is ApiError -> changeErrorUiState(result.message.toString())
                is ApiException -> changeErrorUiState(result.e.message.toString())
                is ApiSuccess -> _uiState.postValue(MyProfileScreenUiState.from(result.data))
            }
        }
    }

    fun onErrorMessageViewed() {
        _uiState.postValue(
            uiState.value!!.copy(
                isError = false,
                errorMessage = "",
            ),
        )
    }

    private fun changeErrorUiState(errorMessage: String) {
        _uiState.postValue(
            uiState.value!!.copy(
                isLoading = false,
                isError = true,
                errorMessage = errorMessage,
            ),
        )
    }

    companion object {
        val factory = ViewModelFactory {
            MyProfileViewModel(
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                memberRepository = KerdyApplication.repositoryContainer.memberRepository,
            )
        }
    }
}
