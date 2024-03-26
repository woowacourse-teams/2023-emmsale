package com.emmsale.presentation.base

import androidx.lifecycle.viewModelScope
import com.emmsale.data.network.callAdapter.ApiResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion

abstract class RefreshableViewModel : NetworkViewModel() {

    abstract fun refresh(): Job

    protected fun <T : Any> fetchData(
        fetchData: suspend () -> ApiResponse<T>,
        onSuccess: suspend (T) -> Unit = {},
        onFailure: suspend (code: Int, message: String?) -> Unit = { _, _ -> dispatchFetchFailEvent() },
        onLoading: suspend () -> Unit = { changeToLoadingState() },
        onNetworkError: suspend () -> Unit = { changeToNetworkErrorState() },
        onStart: suspend () -> Unit = {},
        onFinish: suspend () -> Unit = {},
    ): Job = requestNetwork(
        request = { fetchData() },
        onSuccess = { onSuccess(it) },
        onFailure = { code, message -> onFailure(code, message) },
        onLoading = { onLoading() },
        onNetworkError = { onNetworkError() },
        onStart = { onStart() },
        onFinish = { onFinish() },
    ).launchIn(viewModelScope)

    protected fun <T : Any> refreshData(
        refresh: suspend () -> ApiResponse<T>,
        onSuccess: suspend (T) -> Unit = {},
        onFailure: suspend (code: Int, message: String?) -> Unit = { _, _ -> dispatchFetchFailEvent() },
        onLoading: suspend () -> Unit = {},
        onNetworkError: suspend () -> Unit = { dispatchNetworkErrorEvent() },
        onStart: suspend () -> Unit = {},
        onFinish: suspend () -> Unit = {},
    ): Job = requestNetwork(
        request = { refresh() },
        onSuccess = { onSuccess(it) },
        onFailure = { code, message -> onFailure(code, message) },
        onLoading = { onLoading() },
        onNetworkError = { onNetworkError() },
        onStart = { onStart() },
        onFinish = { onFinish() },
    ).launchIn(viewModelScope)

    protected fun <T : Any> commandAndRefresh(
        command: suspend () -> ApiResponse<T>,
        onSuccess: suspend (T) -> Unit = {},
        onFailure: suspend (code: Int, message: String?) -> Unit = { _, _ -> },
        onLoading: suspend () -> Unit = { delayLoading() },
        onNetworkError: suspend () -> Unit = { dispatchNetworkErrorEvent() },
        onStart: suspend () -> Unit = {},
        onFinish: suspend () -> Unit = {},
    ): Job = requestNetwork(
        request = { command() },
        onSuccess = { onSuccess(it) },
        onFailure = { code, message -> onFailure(code, message) },
        onLoading = { onLoading() },
        onNetworkError = { onNetworkError() },
        onStart = { onStart() },
        onFinish = { onFinish() },
    ).onCompletion { refresh() }.launchIn(viewModelScope)
}
