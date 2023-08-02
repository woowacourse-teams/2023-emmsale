package com.emmsale.presentation.utils.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.emmsale.R
import com.emmsale.presentation.utils.extension.dp

@BindingAdapter("app:imageUrl")
fun ImageView.setImage(imageUrl: String?) {
    Glide.with(this)
        .load(imageUrl)
        .error(R.color.event_thumbnail_default_color)
        .fallback(drawable)
        .into(this)
}

@BindingAdapter("app:imageUrl", "app:roundedImageRadius")
fun ImageView.setRoundedImageUrl(imageUrl: String, radius: Int) {
    Glide.with(this)
        .load(imageUrl)
        .error(R.color.event_thumbnail_default_color)
        .fallback(drawable)
        .transform(CenterCrop(), RoundedCorners(radius.dp))
        .into(this)
}

@BindingAdapter("app:imageUrl", "app:isCircle")
fun ImageView.setCircleImage(imageUrl: String, isCircle: Boolean) {
    if (!isCircle) {
        setImage(imageUrl)
        return
    }
    Glide.with(this)
        .load(imageUrl)
        .error(R.color.event_thumbnail_default_color)
        .fallback(drawable)
        .transform(CenterCrop(), CircleCrop())
        .into(this)
}
