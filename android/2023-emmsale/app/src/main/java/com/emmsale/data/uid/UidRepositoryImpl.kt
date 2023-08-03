package com.emmsale.data.uid

import android.content.SharedPreferences

class UidRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
) : UidRepository {

    override fun getCurrentUid(): Long {
        return sharedPreferences.getLong(UID_KEY, DEFAULT_UID)
    }

    companion object {
        private const val UID_KEY = "uid_key"
        private const val DEFAULT_UID = 0L
    }
}
