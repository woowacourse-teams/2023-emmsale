package com.emmsale.data.config

import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConfigRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val preference: SharedPreferences,
) : ConfigRepository {
    private val preferenceEditor = preference.edit()

    override suspend fun getNotificationReceiveConfig(): Config = withContext(dispatcher) {
        Config(
            isNotificationReceive = preference.getBoolean(KEY_NOTIFICATION_RECEIVE_KEY, false)
        )
    }

    override suspend fun saveNotificationReceiveConfig(isReceive: Boolean) {
        withContext(dispatcher) {
            preferenceEditor.putBoolean(KEY_NOTIFICATION_RECEIVE_KEY, isReceive).apply()
        }
    }

    companion object {
        private const val KEY_NOTIFICATION_RECEIVE_KEY = "notification_receive_key"
    }
}