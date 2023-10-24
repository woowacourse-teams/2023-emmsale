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

@BindingAdapter(
    "app:imageUrl",
    "app:canZoomIn",
    requireAll = true,
)
fun ImageView.setCanZoomInImageUrl(
    imageUrl: String?,
    canZoomIn: Boolean = false,
) {
    Glide.with(this)
        .load(imageUrl)
        .placeholder(R.drawable.img_all_loading)
        .error(R.mipmap.ic_launcher)
        .fallback(R.mipmap.ic_launcher)
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

class MaskTransformation(
    private val mask: Drawable,
) : BitmapTransformation() {

    private val paint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(mask.hashCode().toByte())
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val width = toTransform.width
        val height = toTransform.height

        val bitmap = pool.get(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setHasAlpha(true)

        val canvas = Canvas(bitmap)
        mask.setBounds(0, 0, outWidth, outHeight)
        mask.draw(canvas)
        canvas.drawBitmap(toTransform, 0f, 0f, paint)
        return bitmap
    }
}