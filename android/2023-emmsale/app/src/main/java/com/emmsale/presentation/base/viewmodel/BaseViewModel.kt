package com.emmsale.presentation.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

open class BaseViewModel(
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel(), DispatcherProvider by dispatcherProvider {
    private val _loadingState: MutableLiveData<LoadingUiState> = MutableLiveData<LoadingUiState>()
    val loadingState: LiveData<LoadingUiState> = _loadingState

    protected fun changeLoadingState(loadingUiState: LoadingUiState) {
        _loadingState.postValue(loadingUiState)
    }

    protected inline fun onMain(
        crossinline body: suspend CoroutineScope.() -> Unit,
    ): Job = viewModelScope.launch(main) {
        body(this)
    }

    protected inline fun onIo(
        crossinline body: suspend CoroutineScope.() -> Unit,
    ): Job = viewModelScope.launch(io) {
        body(this)
    }

    protected inline fun onDefault(
        crossinline body: suspend CoroutineScope.() -> Unit,
    ): Job = viewModelScope.launch(default) {
        body(this)
    }
}
