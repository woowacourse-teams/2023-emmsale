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
    requireAll = false
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
    Glide.with(this)
        .load(imageUrl)
        .placeholder(
            loadingImage
                ?: AppCompatResources.getDrawable(context, R.drawable.img_all_loading)
        )
        .error(defaultImage ?: AppCompatResources.getDrawable(context, R.mipmap.ic_launcher))
        .let {
            when {
                mask != null -> it.transform(CenterCrop(), MaskTransformation(mask))
                isCircle == true -> it.transform(CenterCrop(), CircleCrop())
                radius != null -> it.transform(CenterCrop(), RoundedCorners(radius.dp))
                else -> it
            }
        }
        .into(this)

    if (canZoomIn == true) {
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