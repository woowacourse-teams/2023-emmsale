package com.emmsale.presentation.common.extension

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.bumptech.glide.Glide

fun String.toBitmap(context: Context): Bitmap? = Glide
    .with(context)
    .asBitmap()
    .load(Uri.parse(this))
    .submit()
    .get()
