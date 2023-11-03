package com.emmsale.presentation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    protected val _screenUiState = NotNullMutableLiveData(ScreenUiState.NONE)
    val screenUiState: NotNullLiveData<ScreenUiState> = _screenUiState

    protected val _baseUiEvent = SingleLiveEvent<BaseUiEvent>()
    val baseUiEvent: LiveData<BaseUiEvent> = _baseUiEvent

    protected fun changeToLoadingState() {
        _screenUiState.value = ScreenUiState.LOADING
    }

    private fun changeToNetworkErrorState() {
        _screenUiState.value = ScreenUiState.NETWORK_ERROR
    }

    protected fun onRequestFailByNetworkError() {
        _baseUiEvent.value = BaseUiEvent.RequestFailByNetworkError
    }

    protected suspend fun delayLoading(timeMillis: Long = LOADING_DELAY) {
        delay(timeMillis)
        changeToLoadingState()
    }

    fun refresh(): Job = refreshAsync()

    protected abstract fun refreshAsync(): Deferred<ApiResponse<*>>

    protected fun <T : Any> fetchData(
        fetchData: suspend () -> ApiResponse<T>,
        onSuccess: ((T) -> Unit)? = null,
        onFailure: ((code: Int, message: String?) -> Unit)? = null,
        onLoading: (suspend () -> Unit)? = null,
        onNetworkError: (() -> Unit)? = null,
    ): Job = viewModelScope.launch {
        val loadingJob = launch { onLoading?.invoke() ?: changeToLoadingState() }
        when (val result = fetchData()) {
            is Failure -> onFailure?.invoke(result.code, result.message)
            NetworkError -> {
                onNetworkError?.invoke() ?: changeToNetworkErrorState()
                return@launch
            }

            is Success -> onSuccess?.invoke(result.data)
            is Unexpected -> _baseUiEvent.value = BaseUiEvent.Unexpected(result.error.toString())
        }
        loadingJob.cancel()
        _screenUiState.value = ScreenUiState.NONE
    }

    protected fun <T : Any> refreshDataAsync(
        refresh: suspend () -> ApiResponse<T>,
        onSuccess: ((T) -> Unit)? = null,
        onFailure: ((code: Int, message: String?) -> Unit)? = null,
    ): Deferred<ApiResponse<T>> = viewModelScope.async {
        val result = refresh()
        when (result) {
            is Failure -> onFailure?.invoke(result.code, result.message)
            NetworkError -> onRequestFailByNetworkError()
            is Success -> onSuccess?.invoke(result.data)
            is Unexpected -> _baseUiEvent.value = BaseUiEvent.Unexpected(result.error.toString())
        }
        _screenUiState.value = ScreenUiState.NONE
        result
    }

    protected fun <T : Any> command(
        command: suspend () -> ApiResponse<T>,
        onSuccess: ((T) -> Unit)? = null,
        onFailure: ((code: Int, message: String?) -> Unit)? = null,
        onLoading: (suspend () -> Unit)? = null,
    ): Job = viewModelScope.launch {
        val loadingJob = launch { onLoading?.invoke() ?: delayLoading() }
        when (val result = command()) {
            is Failure -> onFailure?.invoke(result.code, result.message)
            NetworkError -> onRequestFailByNetworkError()
            is Success -> onSuccess?.invoke(result.data)
            is Unexpected -> _baseUiEvent.value = BaseUiEvent.Unexpected(result.error.toString())
        }
        loadingJob.cancel()
        _screenUiState.value = ScreenUiState.NONE
    }

    protected fun <T : Any> commandAndRefresh(
        command: suspend () -> ApiResponse<T>,
        onSuccess: ((T) -> Unit)? = null,
        onFailure: ((code: Int, message: String?) -> Unit)? = null,
        onLoading: (suspend () -> Unit)? = null,
        getRefreshResult: (() -> Deferred<ApiResponse<*>>)? = null,
    ): Job = viewModelScope.launch {
        val loadingJob = launch { onLoading?.invoke() ?: delayLoading() }
        when (val result = command()) {
            is Failure -> onFailure?.invoke(result.code, result.message)
            NetworkError -> onRequestFailByNetworkError()
            is Success -> {
                when (val refreshResult = getRefreshResult?.invoke() ?: refreshAsync().await()) {
                    is Failure -> onFailure?.invoke(refreshResult.code, refreshResult.message)
                    NetworkError -> onRequestFailByNetworkError()
                    is Success<*> -> onSuccess?.invoke(result.data)
                    is Unexpected ->
                        _baseUiEvent.value = BaseUiEvent.Unexpected(refreshResult.error.toString())
                }
            }

            is Unexpected -> _baseUiEvent.value = BaseUiEvent.Unexpected(result.error.toString())
        }
        loadingJob.cancel()
        _screenUiState.value = ScreenUiState.NONE
    }

    companion object {
        private const val LOADING_DELAY: Long = 1000
    }
}

enum class ScreenUiState {
    NONE, LOADING, NETWORK_ERROR
}

sealed interface BaseUiEvent {
    data class Unexpected(val errorMessage: String) : BaseUiEvent
    object RequestFailByNetworkError : BaseUiEvent
}
