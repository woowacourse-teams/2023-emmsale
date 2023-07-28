package com.emmsale.presentation.common.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map

class ListLiveData<T> : MutableLiveData<MutableList<T>>() {
    private val items = mutableListOf<T>()

    fun add(item: T) {
        items.add(item)
        updateState()
    }

    fun addAll(newItems: List<T>) {
        items.addAll(newItems)
        updateState()
    }

    fun remove(item: T) {
        items.remove(item)
        updateState()
    }

    fun removeAt(position: Int) {
        items.removeAt(position)
        updateState()
    }

    fun clear() {
        items.clear()
        updateState()
    }

    private fun updateState() {
        postValue(items)
    }

    fun asLiveData(): LiveData<List<T>> = this.map { it }
}
