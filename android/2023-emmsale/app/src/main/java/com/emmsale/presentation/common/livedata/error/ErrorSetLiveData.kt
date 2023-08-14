package com.emmsale.presentation.common.livedata.error

class ErrorSetLiveData<E : ErrorEvent> : ErrorPopLiveData<E>() {
    private val errorEvents: MutableList<E> = mutableListOf()

    fun add(errorEvent: E) {
        errorEvents.remove(errorEvent)
        errorEvents.add(errorEvent)
        postValue(errorEvents)
    }
}
