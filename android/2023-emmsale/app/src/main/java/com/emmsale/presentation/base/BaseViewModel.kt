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

    protected abstract fun onRequestFailByNetworkError()

    abstract fun refresh(): Job

    protected fun <T : Any> requestToNetwork(
        getResult: suspend () -> ApiResponse<T>,
        onSuccess: ((T) -> Unit)? = null,
        onFailure: ((code: Int, message: String?) -> Unit)? = null,
        onLoading: (suspend () -> Unit)? = null,
    ): Job = viewModelScope.launch {
        val loadingJob = launch { onLoading?.invoke() ?: changeToLoadingState() }
        when (val result = getResult()) {
            is Failure -> onFailure?.invoke(result.code, result.message)
            NetworkError -> {
                changeToNetworkErrorState()
                return@launch
            }

            is Success -> onSuccess?.invoke(result.data)
            is Unexpected -> onUnexpected(result.error)
        }
        loadingJob.cancel()
        changeToSuccessState()
    }

    protected fun <T : Any> commandAndRefresh(
        command: suspend () -> ApiResponse<T>,
        onSuccess: ((T) -> Unit)? = null,
        onFailure: ((code: Int, message: String?) -> Unit)? = null,
        onLoading: (suspend () -> Unit)? = null,
    ): Job = viewModelScope.launch {
        val loadingJob = launch { onLoading?.invoke() ?: changeToLoadingState() }
        when (val result = command()) {
            is Failure -> onFailure?.invoke(result.code, result.message)
            NetworkError -> onRequestFailByNetworkError()
            is Success -> {
                refresh().join()
                onSuccess?.invoke(result.data)
            }

            is Unexpected -> onUnexpected(result.error)
        }
        loadingJob.cancel()
        changeToSuccessState()
    }
}
