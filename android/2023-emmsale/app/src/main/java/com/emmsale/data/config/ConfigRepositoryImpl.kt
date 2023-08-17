package com.emmsale.data.config

import android.content.SharedPreferences

class ConfigRepositoryImpl(
    private val preference: SharedPreferences,
) : ConfigRepository {
    private val preferenceEditor = preference.edit()

    override fun getConfig(): Config = Config(
        isNotificationReceive = preference.getBoolean(
            KEY_NOTIFICATION_RECEIVE,
            DEFAULT_VALUE_NOTIFICATION_RECEIVE,
        ),
        isAutoLogin = preference.getBoolean(
            KEY_AUTO_LOGIN,
            DEFAULT_VALUE_AUTO_LOGIN,
        ),
    )

    override fun saveNotificationReceiveConfig(isReceive: Boolean) {
        preferenceEditor.putBoolean(KEY_NOTIFICATION_RECEIVE, isReceive).apply()
    }

    override fun saveAutoLoginConfig(isAutoLogin: Boolean) {
        preferenceEditor.putBoolean(KEY_AUTO_LOGIN, isAutoLogin).apply()
    }

    companion object {
        private const val KEY_NOTIFICATION_RECEIVE = "notification_receive_key"
        private const val DEFAULT_VALUE_NOTIFICATION_RECEIVE = true

        private const val KEY_AUTO_LOGIN = "auto_login_key"
        private const val DEFAULT_VALUE_AUTO_LOGIN = false
    }
}
