package com.emmsale.presentation.common.extension

import android.Manifest
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.emmsale.R
import com.emmsale.presentation.common.views.ConfirmDialog
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

fun AppCompatActivity.showPermissionRequestDialog(
    onConfirm: () -> Unit = {},
    onDenied: () -> Unit = {},
) {
    ConfirmDialog(
        context = this,
        title = getString(R.string.login_post_notification_permission_needed_title),
        message = getString(R.string.login_post_notification_permission_needed_message),
        onPositiveButtonClick = onConfirm,
        onNegativeButtonClick = onDenied,
    ).show()
}

fun AppCompatActivity.navigateToNotificationSettings(launcher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, baseContext.packageName)
    }
    launcher.launch(intent)
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
    channelId: Int,
    intent: Intent? = null,
) {
    val notificationManager = NotificationManagerCompat.from(this)

    if (!checkPostNotificationPermission()) return

    val notification = createNotification(channelId, title, message, notificationId, intent)
    notificationManager.notify(notificationId, notification)
}

private fun Context.createNotification(
    channelId: Int,
    title: String,
    message: String,
    notificationId: Int,
    intent: Intent? = null,
) = NotificationCompat.Builder(this, channelId.toString())
    .setSmallIcon(R.drawable.ic_all_notification)
    .setContentTitle(title)
    .setContentText(message)
    .setContentIntent(
        PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_MUTABLE),
    )
    .setPriority(NotificationCompat.PRIORITY_HIGH)
    .setAutoCancel(true)
    .build()
