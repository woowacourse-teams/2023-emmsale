package com.emmsale.presentation.common.extension

import android.Manifest
import android.app.ActivityManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.emmsale.R
import com.emmsale.presentation.common.views.ConfirmDialog
import com.google.firebase.messaging.FirebaseMessagingService
import java.time.LocalDate

private const val PACKAGE = "package"
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

fun Context.showPermissionRequestDialog(
    message: String = getString(R.string.login_post_notification_permission_needed_message),
    title: String = getString(R.string.login_post_notification_permission_needed_title),
    onConfirm: () -> Unit = {},
    onDenied: () -> Unit = {},
) {
    ConfirmDialog(
        context = this,
        title = title,
        message = message,
        onPositiveButtonClick = onConfirm,
        onNegativeButtonClick = onDenied,
        onCancel = onDenied,
    ).show()
}

fun Context.navigateToNotificationSettings(launcher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    }
    launcher.launch(intent)
}

fun Context.navigateToApplicationDetailSetting(launcher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts(PACKAGE, packageName, null)
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
    intent: Intent = Intent(),
    largeIconUrl: String? = null,
    groupKey: String? = null,
) {
    val notificationManager = NotificationManagerCompat.from(this)

    if (!checkPostNotificationPermission()) return

    val notification = createNotification(
        channelId = channelId,
        title = title,
        message = message,
        notificationId = notificationId,
        intent = intent,
        largeIconUrl = largeIconUrl,
        groupKey = groupKey,
    )
    notificationManager.notify(notificationId, notification)
}

private fun Context.createNotification(
    channelId: Int,
    title: String,
    message: String,
    notificationId: Int,
    intent: Intent = Intent(),
    largeIconUrl: String? = null,
    groupKey: String? = null,
) = NotificationCompat.Builder(this, channelId.toString())
    .setSmallIcon(R.drawable.ic_all_notification)
    .setColor(ContextCompat.getColor(this, R.color.notification_icon_background_color))
    .setLargeIcon(largeIconUrl?.toBitmap(this))
    .setContentTitle(title)
    .setGroupSummary(true)
    .setGroup(groupKey)
    .setContentText(message)
    .setContentIntent(getPendingIntent(intent, notificationId))
    .setAutoCancel(true)
    .build()

private fun Context.getPendingIntent(
    intent: Intent,
    notificationId: Int,
): PendingIntent? = TaskStackBuilder.create(this).run {
    addNextIntentWithParentStack(intent)
    getPendingIntent(
        notificationId,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}

val Context.topActivityName: String?
    get() {
        val manager = getSystemService(FirebaseMessagingService.ACTIVITY_SERVICE) as ActivityManager
        return manager.appTasks.getOrNull(0)?.taskInfo?.topActivity?.className
    }
