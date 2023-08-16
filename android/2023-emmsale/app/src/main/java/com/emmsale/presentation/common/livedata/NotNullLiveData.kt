package com.emmsale.presentation.common.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

open class NotNullLiveData<T : Any>(value: T) : LiveData<T>(value) {
    override fun getValue(): T {
        return super.getValue() as T
    }

    inline fun observe(owner: LifecycleOwner, crossinline onChanged: (t: T) -> Unit) {
        this.observe(
            owner,
            Observer { value -> value?.let(onChanged) },
        )
    }
}
