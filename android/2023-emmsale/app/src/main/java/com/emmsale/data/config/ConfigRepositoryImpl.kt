package com.emmsale.data.config

import android.content.SharedPreferences

class ConfigRepositoryImpl(
    private val preference: SharedPreferences,
) : ConfigRepository {
    private val preferenceEditor = preference.edit()

    override fun getConfig(): Config = Config(
        isNotificationReceive = preference.getBoolean(KEY_NOTIFICATION_RECEIVE_KEY, true),
    )

    override fun saveNotificationReceiveConfig(isReceive: Boolean) {
        preferenceEditor.putBoolean(KEY_NOTIFICATION_RECEIVE_KEY, isReceive).apply()
    }

    companion object {
        private const val KEY_NOTIFICATION_RECEIVE_KEY = "notification_receive_key"
    }
}
