package com.emmsale.presentation.utils.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.emmsale.R
import com.emmsale.presentation.utils.extension.px

@BindingAdapter(
    "app:imageUrl",
    "app:roundedImageRadius",
    requireAll = false,
)
fun ImageView.setRoundedImageUrl(
    imageUrl: String? = null,
    radius: Int = 0,
) {
    Glide.with(this)
        .load(imageUrl)
        .error(R.color.event_thumbnail_default_color)
        .fallback(drawable)
        .transform(CenterCrop(), RoundedCorners(radius.px))
        .into(this)
}
