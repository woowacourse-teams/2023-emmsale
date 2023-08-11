package com.emmsale.presentation.common.livedata

class NotNullMutableLiveData<T : Any>(value: T) : NotNullLiveData<T>(value) {
    public override fun setValue(value: T) {
        super.setValue(value)
    }

    public override fun postValue(value: T) {
        super.postValue(value)
    }
}
