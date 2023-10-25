package com.emmsale.presentation.common.imageUtil

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream

fun getFileFromUri(context: Context, uri: Uri): File {
    val bitmap = uri.getBitmapFromUri(context)
    val file = bitmap?.convertToFile(context)
    return file ?: File("")
}

private fun Uri.getBitmapFromUri(context: Context): Bitmap? {
    return context.contentResolver.openInputStream(this)?.use {
        BitmapFactory.decodeStream(it)
    }
}

private fun Bitmap.convertToFile(context: Context): File {
    val tempFile = File.createTempFile("imageFile", ".jpg", context.cacheDir)

    FileOutputStream(tempFile).use { fileOutputStream ->
        this.compress(Bitmap.CompressFormat.JPEG, HIGHEST_COMPRESS_QUALITY, fileOutputStream)
    }
    return tempFile
}

fun AppCompatActivity.checkImagePermission(
    onGranted: () -> Unit,
    onFirstDenied: (String) -> Unit,
    onDenied: () -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        checkImagePermission(
            Manifest.permission.READ_MEDIA_IMAGES,
            onGranted,
            onFirstDenied,
            onDenied,
        )
    } else {
        checkImagePermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            onGranted,
            onFirstDenied,
            onDenied,
        )
    }
}

fun AppCompatActivity.checkImagePermission(
    permission: String,
    onGranted: () -> Unit,
    onFirstDenied: (String) -> Unit,
    onDenied: () -> Unit,
) {
    when {
        isImageAccessPermissionGranted() -> onGranted()

        !isImageAccessPermissionGranted() &&
            shouldShowRequestPermissionRationale(permission) -> onFirstDenied(permission)

        else -> onDenied()
    }
}

fun Context.isImageAccessPermissionGranted(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES,
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        ) == PackageManager.PERMISSION_GRANTED
    }
}

private const val HIGHEST_COMPRESS_QUALITY = 100
