package com.emmsale.presentation.utils.bindingadapter

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("imageUrl", "error", requireAll = false)
fun loadImage(imageView: ImageView, imageUrl: String, errorImage: Drawable?) {
    Glide.with(imageView.context)
        .load(imageUrl)
        .error(errorImage)
        .into(imageView)
}