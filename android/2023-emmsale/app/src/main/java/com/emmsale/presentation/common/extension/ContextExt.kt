package com.emmsale.presentation.common.extension

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.emmsale.R
import java.time.LocalDate

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
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
        message(getString(R.string.post_notification_permission_needed_message))
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
