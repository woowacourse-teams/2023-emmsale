package com.emmsale.presentation.common.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.emmsale.R
import com.emmsale.presentation.common.extension.dp
import com.stfalcon.imageviewer.StfalconImageViewer

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

@BindingAdapter("app:imageUrl", "app:isCircle")
fun ImageView.setCircleImage(imageUrl: String?, isCircle: Boolean) {
    if (!isCircle) {
        setImage(imageUrl)
        return
    }

    Glide.with(this)
        .load(imageUrl)
        .placeholder(R.drawable.img_all_loading)
        .error(R.mipmap.ic_launcher)
        .fallback(R.mipmap.ic_launcher)
        .transform(CenterCrop(), CircleCrop())
        .into(this)
}

@BindingAdapter(
    "app:imageUrl",
    "app:roundedImageRadius",
    "app:canZoomIn",
    requireAll = true,
)
fun ImageView.setCanZoomInRoundedImageUrl(
    imageUrl: String?,
    radius: Int,
    canZoomIn: Boolean,
) {
    Glide.with(this)
        .load(imageUrl)
        .placeholder(R.drawable.img_all_loading)
        .error(R.mipmap.ic_launcher)
        .fallback(R.mipmap.ic_launcher)
        .transform(CenterCrop(), RoundedCorners(radius.dp))
        .into(this)

    if (!canZoomIn) return

    setOnClickListener {
        StfalconImageViewer.Builder(this.context, listOf(imageUrl)) { view, image ->
            Glide.with(this.context)
                .load(image)
                .placeholder(R.drawable.img_all_loading)
                .error(R.mipmap.ic_launcher)
                .into(view)
        }.show()
    }
}
