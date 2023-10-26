package com.emmsale.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    protected abstract fun changeToLoadingState()

    protected abstract fun changeToNetworkErrorState()

    protected abstract fun changeToSuccessState()

    protected abstract fun onUnexpected(throwable: Throwable?)

    protected fun <T : Any> handleResponse(
        getResult: suspend () -> ApiResponse<T>,
        onSuccess: (T) -> Unit,
        onFailure: (code: Int, message: String?) -> Unit,
    ): Job = viewModelScope.launch {
        changeToLoadingState()
        when (val result = getResult()) {
            is Failure -> onFailure(result.code, result.message)

            NetworkError -> {
                changeToNetworkErrorState()
                return@launch
            }

            is Success -> onSuccess(result.data)

            is Unexpected -> onUnexpected(result.error)
        }
        changeToSuccessState()
    }
}