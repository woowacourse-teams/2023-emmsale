package com.emmsale.presentation.common.livedata.error

import androidx.lifecycle.MutableLiveData

open class ErrorClearLiveData<E : ErrorEvent> : MutableLiveData<List<E>>() {
    private val errorEvents: MutableList<E> = mutableListOf()

    fun clear() {
        errorEvents.clear()
        postValue(errorEvents)
    }
}
