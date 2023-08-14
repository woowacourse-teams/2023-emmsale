package com.emmsale.presentation.common.livedata.error

import androidx.lifecycle.MutableLiveData

open class ErrorPopLiveData<E : ErrorEvent> : MutableLiveData<List<E>>() {
    private val errorEvents: MutableList<E> = mutableListOf()

    fun pop(): E {
        val errorEvent = errorEvents.removeFirst()
        postValue(errorEvents)
        return errorEvent
    }
}
