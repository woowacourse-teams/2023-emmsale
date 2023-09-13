package com.emmsale.presentation

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.emmsale.R
import com.emmsale.data.common.ServiceFactory
import com.emmsale.di.RepositoryContainer
import com.emmsale.di.ServiceContainer
import com.emmsale.di.SharedPreferenceContainer
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class KerdyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initDiContainer()
        initFirebaseAnalytics()
        initNotificationChannels()
    }

    private fun initDiContainer() {
        repositoryContainer = RepositoryContainer(
            serviceContainer = ServiceContainer(ServiceFactory()),
            preferenceContainer = SharedPreferenceContainer(this),
        )
    }

    private fun initFirebaseAnalytics() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    private fun initNotificationChannels() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannels = listOf(
            createFollowNotificationChannel(),
            createChildCommentNotificationChannel(),
            createInterestEventNotificationChannel(),
        )
        notificationManager.createNotificationChannels(notificationChannels)
    }

    private fun createFollowNotificationChannel(): NotificationChannel {
        val channelId = R.id.id_all_follow_notification_channel
        val channelName =
            getString(R.string.kerdyfirebasemessaging_follow_notification_channel_name)
        val channelDescription =
            getString(R.string.kerdyfirebasemessaging_follow_notification_channel_description)
        return NotificationChannel(
            channelId.toString(),
            channelName,
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = channelDescription
        }
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
            getString(R.string.kerdyfirebasemessaging_follow_notification_channel_description)
        return NotificationChannel(
            channelId.toString(),
            channelName,
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = channelDescription
        }
    }

    companion object {
        lateinit var repositoryContainer: RepositoryContainer
            private set
        lateinit var firebaseAnalytics: FirebaseAnalytics
            private set
        val applicationScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    }
}
