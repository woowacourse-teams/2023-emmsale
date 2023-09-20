package com.emmsale.presentation.common.bindingadapter

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
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
        .placeholder(R.drawable.img_all_loading)
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
        .placeholder(R.drawable.img_all_loading)
        .error(R.mipmap.ic_launcher)
        .fallback(R.mipmap.ic_launcher)
        .transform(CenterCrop(), RoundedCorners(radius.dp))
        .into(this)
}

@BindingAdapter(
    "app:imageUrl",
    "app:isCircle",
    "app:loadingImage",
    "app:defaultImage",
    requireAll = false,
)
fun ImageView.setCircleImage(
    imageUrl: String?,
    isCircle: Boolean,
    loadingImageRes: Drawable? = ContextCompat.getDrawable(context, R.drawable.img_all_loading),
    defaultImageRes: Drawable? = ContextCompat.getDrawable(context, R.mipmap.ic_launcher),
) {
    if (!isCircle) {
        setImage(imageUrl)
        return
    }

    Glide.with(this)
        .load(imageUrl)
        .placeholder(loadingImageRes)
        .error(defaultImageRes)
        .fallback(defaultImageRes)
        .transform(CenterCrop(), CircleCrop())
        .into(this)
}
