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
    requireAll = true,
)
fun ImageView.setRoundedImageUrl(
    imageUrl: String?,
    radius: Int,
) {
    Glide.with(this)
        .load(imageUrl)
        .error(R.color.event_thumbnail_default_color)
        .fallback(R.color.event_thumbnail_default_color)
        .transform(CenterCrop(), RoundedCorners(radius.px))
        .into(this)
}
