package com.emmsale.presentation.utils.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map

class DistinctListLiveData<T> : MutableLiveData<MutableList<T>>() {
    private var items = mutableListOf<T>()

    fun add(item: T) {
        items.add(item)
        val distinctItems = items.distinct().toMutableList()
        items.clear()
        items.addAll(distinctItems)

        postValue(distinctItems)
        updateState()
    }

    fun addAll(newItems: List<T>) {
        items.addAll(newItems)
        val distinctItems = items.distinct().toMutableList()
        items.clear()
        items.addAll(distinctItems)

        postValue(distinctItems)
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
        value = items
    }

    fun asLiveData(): LiveData<List<T>> = this.map { it }
}
