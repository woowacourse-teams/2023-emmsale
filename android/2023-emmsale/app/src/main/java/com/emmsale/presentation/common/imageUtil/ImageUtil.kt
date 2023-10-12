package com.emmsale.presentation.common.imageUtil

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

fun Uri.convertToAbsolutePath(context: Context): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor: Cursor? = context.contentResolver.query(
        this,
        projection,
        null,
        null,
        null,
    )
    return cursor?.use {
        val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        it.moveToFirst()
        it.getString(columnIndex)
    }
}
