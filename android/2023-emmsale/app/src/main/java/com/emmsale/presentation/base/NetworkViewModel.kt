package com.emmsale.presentation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.presentation.common.CommonUiEvent
import com.emmsale.presentation.common.ScreenUiState
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class NetworkViewModel : ViewModel() {

    protected val _screenUiState = NotNullMutableLiveData(ScreenUiState.NONE)
    val screenUiState: NotNullLiveData<ScreenUiState> = _screenUiState

    protected val _commonUiEvent = SingleLiveEvent<CommonUiEvent>()
    val commonUiEvent: LiveData<CommonUiEvent> = _commonUiEvent

    protected fun changeToLoadingState() {
        _screenUiState.value = ScreenUiState.LOADING
    }

    protected fun changeToNetworkErrorState() {
        _screenUiState.value = ScreenUiState.NETWORK_ERROR
    }

    protected fun dispatchNetworkErrorEvent() {
        _commonUiEvent.value = CommonUiEvent.RequestFailByNetworkError
    }

    protected suspend fun delayLoading(timeMillis: Long = LOADING_DELAY) {
        delay(timeMillis)
        changeToLoadingState()
    }

    abstract fun refresh(): Job

    protected fun <T : Any> fetchData(
        fetchData: suspend () -> ApiResponse<T>,
        onSuccess: (T) -> Unit = {},
        onFailure: (code: Int, message: String?) -> Unit = { _, _ -> },
        onLoading: suspend () -> Unit = suspend { changeToLoadingState() },
        onNetworkError: () -> Unit = ::changeToNetworkErrorState,
    ): Job = viewModelScope.launch {
        val loadingJob = launch { onLoading() }
        when (val result = fetchData()) {
            is Success -> onSuccess(result.data)
            is Failure -> onFailure(result.code, result.message)
            NetworkError -> {
                onNetworkError()
                return@launch
            }

            is Unexpected ->
                _commonUiEvent.value = CommonUiEvent.Unexpected(result.error.toString())
        }
        loadingJob.cancel()
        _screenUiState.value = ScreenUiState.NONE
    }

    protected fun <T : Any> refreshData(
        refresh: suspend () -> ApiResponse<T>,
        onSuccess: (T) -> Unit = {},
        onFailure: (code: Int, message: String?) -> Unit = { _, _ -> },
    ): Job = viewModelScope.launch {
        when (val result = refresh()) {
            is Success -> onSuccess(result.data)
            is Failure -> onFailure(result.code, result.message)
            NetworkError -> {
                dispatchNetworkErrorEvent()
                return@launch
            }

            is Unexpected ->
                _commonUiEvent.value = CommonUiEvent.Unexpected(result.error.toString())
        }
        _screenUiState.value = ScreenUiState.NONE
    }

    protected fun <T : Any> command(
        command: suspend () -> ApiResponse<T>,
        onSuccess: (T) -> Unit = {},
        onFailure: (code: Int, message: String?) -> Unit = { _, _ -> },
        onLoading: suspend () -> Unit = suspend { delayLoading() },
    ): Job = viewModelScope.launch {
        val loadingJob = launch { onLoading() }
        when (val result = command()) {
            is Success -> onSuccess(result.data)
            is Failure -> onFailure(result.code, result.message)
            NetworkError -> dispatchNetworkErrorEvent()
            is Unexpected ->
                _commonUiEvent.value = CommonUiEvent.Unexpected(result.error.toString())
        }
        loadingJob.cancel()
        _screenUiState.value = ScreenUiState.NONE
    }

    protected fun <T : Any> commandAndRefresh(
        command: suspend () -> ApiResponse<T>,
        onSuccess: (T) -> Unit = {},
        onFailure: (code: Int, message: String?) -> Unit = { _, _ -> },
        onLoading: suspend () -> Unit = suspend { delayLoading() },
        refresh: () -> Job = { this@NetworkViewModel.refresh() },
    ): Job = viewModelScope.launch {
        val loadingJob = launch { onLoading() }
        when (val result = command()) {
            is Success -> {
                refresh().join()
                onSuccess(result.data)
            }

            is Failure -> onFailure(result.code, result.message)
            NetworkError -> dispatchNetworkErrorEvent()

            is Unexpected ->
                _commonUiEvent.value = CommonUiEvent.Unexpected(result.error.toString())
        }
        loadingJob.cancel()
        _screenUiState.value = ScreenUiState.NONE
    }

    companion object {
        private const val LOADING_DELAY: Long = 1000
    }
}
