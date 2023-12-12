package com.emmsale.data.repository.concretes

import android.content.SharedPreferences
import com.emmsale.data.model.Config
import com.emmsale.data.repository.interfaces.ConfigRepository
import javax.inject.Inject

class DefaultConfigRepository @Inject constructor(
    private val preference: SharedPreferences,
) : ConfigRepository {
    private val preferenceEditor = preference.edit()

    override fun getConfig(): Config = Config(
        isNotificationReceive = preference.getBoolean(
            KEY_NOTIFICATION_RECEIVE,
            DEFAULT_VALUE_NOTIFICATION_RECEIVE,
        ),
        isCommentNotificationReceive = preference.getBoolean(
            KEY_CHILD_COMMENT_NOTIFICATION_RECEIVE,
            DEFAULT_VALUE_CHILD_COMMENT_NOTIFICATION_RECEIVE,
        ),
        isInterestEventNotificationReceive = preference.getBoolean(
            KEY_INTEREST_EVENT_NOTIFICATION_RECEIVE,
            DEFAULT_VALUE_INTEREST_EVENT_NOTIFICATION_RECEIVE,
        ),
        isAutoLogin = preference.getBoolean(
            KEY_AUTO_LOGIN,
            DEFAULT_VALUE_AUTO_LOGIN,
        ),
        isMessageNotificationReceive = preference.getBoolean(
            KEY_MESSAGE_NOTIFICATION_RECEIVE,
            DEFAULT_VALUE_MESSAGE_NOTIFICATION_RECEIVE,
        ),
    )

    override fun saveAllNotificationReceiveConfig(isReceive: Boolean) {
        preferenceEditor.putBoolean(KEY_NOTIFICATION_RECEIVE, isReceive).commit()
    }

    override fun saveCommentNotificationReceiveConfig(isReceive: Boolean) {
        preferenceEditor.putBoolean(KEY_CHILD_COMMENT_NOTIFICATION_RECEIVE, isReceive).commit()
    }

    override fun saveInterestEventNotificationReceiveConfig(isReceive: Boolean) {
        preferenceEditor.putBoolean(KEY_INTEREST_EVENT_NOTIFICATION_RECEIVE, isReceive).commit()
    }

    override fun saveMessageNotificationReceiveConfig(isReceive: Boolean) {
        preferenceEditor.putBoolean(KEY_MESSAGE_NOTIFICATION_RECEIVE, isReceive).commit()
    }

    override fun saveAutoLoginConfig(isAutoLogin: Boolean) {
        preferenceEditor.putBoolean(KEY_AUTO_LOGIN, isAutoLogin).commit()
    }

    companion object {
        private const val KEY_NOTIFICATION_RECEIVE = "notification_receive_key"
        private const val DEFAULT_VALUE_NOTIFICATION_RECEIVE = true

        private const val KEY_AUTO_LOGIN = "auto_login_key"
        private const val DEFAULT_VALUE_AUTO_LOGIN = false

        private const val KEY_CHILD_COMMENT_NOTIFICATION_RECEIVE =
            "child_comment_notification_receive_key"
        private const val DEFAULT_VALUE_CHILD_COMMENT_NOTIFICATION_RECEIVE = true

        private const val KEY_INTEREST_EVENT_NOTIFICATION_RECEIVE =
            "interest_event_notification_receive_key"
        private const val DEFAULT_VALUE_INTEREST_EVENT_NOTIFICATION_RECEIVE = true

        private const val KEY_MESSAGE_NOTIFICATION_RECEIVE =
            "message_notification_receive_key"
        private const val DEFAULT_VALUE_MESSAGE_NOTIFICATION_RECEIVE = true
    }
}
