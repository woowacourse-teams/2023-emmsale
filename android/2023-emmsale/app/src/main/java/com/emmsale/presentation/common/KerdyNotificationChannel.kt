package com.emmsale.presentation.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.StringRes
import com.emmsale.R

enum class KerdyNotificationChannel(
    private val channelId: String,
    @StringRes private val channelNameResId: Int,
    @StringRes private val channelDescResId: Int,
    private val channelImportance: Int = NotificationManager.IMPORTANCE_HIGH,
) {
    CHILD_COMMENT(
        channelId = R.id.id_all_child_comment_notification_channel.toString(),
        channelNameResId = R.string.kerdyfirebasemessaging_child_comment_notification_channel_name,
        channelDescResId = R.string.kerdyfirebasemessaging_child_comment_notification_channel_description,
    ),
    INTEREST_EVENT(
        channelId = R.id.id_all_interest_event_notification_channel.toString(),
        channelNameResId = R.string.kerdyfirebasemessaging_interest_event_notification_channel_name,
        channelDescResId = R.string.kerdyfirebasemessaging_interest_event_notification_channel_description,
    ),
    MESSAGE(
        channelId = R.id.id_all_message_notification_channel.toString(),
        channelNameResId = R.string.kerdyfirebasemessaging_message_notification_channel_name,
        channelDescResId = R.string.kerdyfirebasemessaging_message_notification_channel_description,
    ),
    ;

    fun createChannel(context: Context): NotificationChannel = NotificationChannel(
        channelId,
        context.getString(channelNameResId),
        channelImportance,
    ).apply {
        description = context.getString(channelDescResId)
    }

    companion object {
        fun createChannels(
            context: Context,
        ): List<NotificationChannel> = KerdyNotificationChannel
            .values()
            .map { channel -> channel.createChannel(context) }
    }
}
