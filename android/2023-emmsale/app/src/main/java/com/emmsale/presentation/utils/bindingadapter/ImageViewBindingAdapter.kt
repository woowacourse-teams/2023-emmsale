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
        .placeholder(R.drawable.img_all_error)
        .error(R.drawable.img_all_error)
        .fallback(R.drawable.img_all_error)
        .transform(CenterCrop(), RoundedCorners(radius.px))
        .into(this)
}
