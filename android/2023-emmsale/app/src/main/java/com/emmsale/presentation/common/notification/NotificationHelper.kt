package com.emmsale.presentation.common.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.emmsale.R

class NotificationHelper(private val context: Context) {
    private val notificationManager = NotificationManagerCompat.from(context)

    fun showNotification(
        title: String,
        message: String,
        notificationId: Int = 0,
        channelId: String,
        channelName: String,
        channelDescription: String,
    ) {
        createNotificationChannel(channelId, channelName, channelDescription)
        val notification = createNotification(title, message, channelId)
        if (checkPostNotificationPermission()) {
            notificationManager.notify(notificationId, notification)
        }
    }

    private fun checkPostNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createNotification(title: String, message: String, channelId: String) =
        NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

    private fun createNotificationChannel(
        channelId: String,
        channelName: String,
        channelDescription: String
    ) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }
        notificationManager.createNotificationChannel(channel)
    }
}
