package com.emmsale.presentation.common.extension

import android.Manifest
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.emmsale.R
import java.time.LocalDate

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(@StringRes textResId: Int) {
    showToast(getString(textResId))
}

fun Context.checkPostNotificationPermission(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
    return ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.POST_NOTIFICATIONS,
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.showPermissionRequestDialog() {
    showDialog {
        message(getString(R.string.login_post_notification_permission_needed_message))
        positiveButton { navigateToApplicationSettings() }
        negativeButton { }
    }
}

fun Context.navigateToApplicationSettings() {
    val settingIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .setData(Uri.parse("package:$packageName"))
        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(settingIntent)
}

fun Context.showDatePickerDialog(block: (date: LocalDate) -> Unit) {
    val todayDate = LocalDate.now()
    DatePickerDialog(
        this,
        R.style.DatePickerDialogTheme,
        { _, year, month, dayOfMonth ->
            block(LocalDate.of(year, month + 1, dayOfMonth))
        },
        todayDate.year,
        todayDate.monthValue - 1,
        todayDate.dayOfMonth,
    ).show()
}

fun Context.showNotification(
    title: String,
    message: String,
    notificationId: Int = System.currentTimeMillis().toInt(),
    channelId: String,
    channelName: String,
    channelDescription: String,
    intent: Intent? = null,
) {
    val notificationManager = NotificationManagerCompat.from(this)

    if (checkPostNotificationPermission()) {
        createNotificationChannel(
            notificationManager,
            channelId,
            channelName,
            channelDescription,
        )
        val notification = createNotification(channelId, title, message, intent)
        notificationManager.notify(notificationId, notification)
    }
}

private fun createNotificationChannel(
    notificationManager: NotificationManagerCompat,
    channelId: String,
    channelName: String,
    channelDescription: String,
) {
    val importance = NotificationManager.IMPORTANCE_HIGH
    val channel = NotificationChannel(channelId, channelName, importance).apply {
        description = channelDescription
    }
    notificationManager.createNotificationChannel(channel)
}

private fun Context.createNotification(
    channelId: String,
    title: String,
    message: String,
    intent: Intent? = null,
) = NotificationCompat.Builder(this, channelId)
    .setSmallIcon(R.drawable.ic_launcher_foreground)
    .setContentTitle(title)
    .setContentText(message)
    .setContentIntent(
        intent?.let {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        },
    )
    .setPriority(NotificationCompat.PRIORITY_HIGH)
    .setAutoCancel(true)
    .build()
