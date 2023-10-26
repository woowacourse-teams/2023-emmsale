package com.emmsale.presentation.common.bindingadapter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.emmsale.R
import com.emmsale.presentation.common.extension.dp
import com.stfalcon.imageviewer.StfalconImageViewer
import java.security.MessageDigest

@BindingAdapter(
    "app:imageUrl",
    "app:loadingImage",
    "app:defaultImage",
    "app:mask",
    "app:isCircle",
    "app:roundedImageRadius",
    "app:canZoomIn",
    requireAll = false,
)
fun ImageView.setImage(
    imageUrl: String?,
    loadingImage: Drawable?,
    defaultImage: Drawable?,
    mask: Drawable?,
    isCircle: Boolean?,
    radius: Int?,
    canZoomIn: Boolean?,
) {
    val selectedLoadingImage =
        loadingImage ?: AppCompatResources.getDrawable(context, R.drawable.img_all_loading)
    val selectedDefaultImage =
        defaultImage ?: AppCompatResources.getDrawable(context, R.mipmap.ic_launcher)

    Glide.with(this)
        .load(imageUrl)
        .placeholder(selectedLoadingImage)
        .error(selectedDefaultImage)
        .customTransform(mask = mask, isCircle = isCircle, radius = radius)
        .into(this)

    if (canZoomIn == true) {
        makeItPossibleToZoomInOnClick(
            imageUrl = imageUrl,
            loadingImage = selectedLoadingImage,
            defaultImage = defaultImage,
        )
    }
}

// 순서는 변화될 사진 모양의 구체적인 수준에 따라 설정했습니다.
private fun RequestBuilder<Drawable>.customTransform(
    mask: Drawable?,
    isCircle: Boolean?,
    radius: Int?,
): RequestBuilder<Drawable> = when {
    mask != null -> transform(CenterCrop(), MaskTransformation(mask))
    radius != null -> transform(CenterCrop(), RoundedCorners(radius.dp))
    isCircle == true -> transform(CenterCrop(), CircleCrop())
    else -> this
}

private fun ImageView.makeItPossibleToZoomInOnClick(
    imageUrl: String?,
    loadingImage: Drawable?,
    defaultImage: Drawable?,
) {
    setOnClickListener {
        StfalconImageViewer.Builder(this.context, listOf(imageUrl)) { view, image ->
            Glide.with(this.context)
                .load(image)
                .placeholder(loadingImage)
                .error(defaultImage)
                .into(view)
        }.show()
    }
}

private class MaskTransformation(
    private val mask: Drawable,
) : BitmapTransformation() {

    private val paint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }

    override fun equals(other: Any?): Boolean {
        return other is MaskTransformation &&
            other.mask == mask
    }

    override fun hashCode(): Int {
        return ID.hashCode() + mask.hashCode()
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update((ID + mask.toString()).toByteArray(CHARSET))
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int,
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

    companion object {
        private const val ID =
            "com.emmsale.presentation.common.bindingadapter.ImageViewBindingAdapter"
    }
}
