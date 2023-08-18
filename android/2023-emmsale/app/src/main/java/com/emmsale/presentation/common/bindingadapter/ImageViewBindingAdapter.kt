package com.emmsale.presentation.common.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.emmsale.R
import com.emmsale.presentation.common.extension.dp

@BindingAdapter("app:imageUrl")
fun ImageView.setImage(imageUrl: String?) {
    Glide.with(this)
        .load(imageUrl)
        .placeholder(R.mipmap.ic_launcher)
        .error(R.mipmap.ic_launcher)
        .fallback(R.mipmap.ic_launcher)
        .into(this)
}

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
        .placeholder(R.mipmap.ic_launcher)
        .error(R.mipmap.ic_launcher)
        .fallback(R.mipmap.ic_launcher)
        .transform(CenterCrop(), RoundedCorners(radius.dp))
        .into(this)
}

@BindingAdapter("app:imageUrl", "app:isCircle")
fun ImageView.setCircleImage(imageUrl: String?, isCircle: Boolean) {
    if (!isCircle) {
        setImage(imageUrl)
        return
    }

    Glide.with(this)
        .load(imageUrl)
        .placeholder(R.mipmap.ic_launcher)
        .error(R.mipmap.ic_launcher)
        .fallback(R.mipmap.ic_launcher)
        .transform(CenterCrop(), CircleCrop())
        .into(this)
}
