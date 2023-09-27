package com.emmsale.presentation

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.emmsale.R
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KerdyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initFirebaseAnalytics()
        initNotificationChannels()
    }

    private fun initFirebaseAnalytics() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    private fun initNotificationChannels() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannels = listOf(
            createChildCommentNotificationChannel(),
            createInterestEventNotificationChannel(),
            createMessageNotificationChannel(),
        )
        notificationManager.createNotificationChannels(notificationChannels)
    }

    private fun createChildCommentNotificationChannel(): NotificationChannel {
        val channelId = R.id.id_all_child_comment_notification_channel
        val channelName =
            getString(R.string.kerdyfirebasemessaging_child_comment_notification_channel_name)
        val channelDescription =
            getString(R.string.kerdyfirebasemessaging_child_comment_notification_channel_description)
        return NotificationChannel(
            channelId.toString(),
            channelName,
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = channelDescription
        }
    }

    private fun createInterestEventNotificationChannel(): NotificationChannel {
        val channelId = R.id.id_all_interest_event_notification_channel
        val channelName =
            getString(R.string.kerdyfirebasemessaging_interest_event_notification_channel_name)
        val channelDescription =
            getString(R.string.kerdyfirebasemessaging_interest_event_notification_channel_description)
        return NotificationChannel(
            channelId.toString(),
            channelName,
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = channelDescription
        }
    }

    private fun createMessageNotificationChannel(): NotificationChannel {
        val channelId = R.id.id_all_message_notification_channel
        val channelName =
            getString(R.string.kerdyfirebasemessaging_message_notification_channel_name)
        val channelDescription =
            getString(R.string.kerdyfirebasemessaging_message_notification_channel_description)
        return NotificationChannel(
            channelId.toString(),
            channelName,
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = channelDescription
        }
    }

    companion object {
        lateinit var firebaseAnalytics: FirebaseAnalytics
            private set
    }
}
