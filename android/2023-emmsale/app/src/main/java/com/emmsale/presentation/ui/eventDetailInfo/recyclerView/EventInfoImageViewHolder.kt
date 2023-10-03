package com.emmsale.presentation.ui.eventDetailInfo.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.emmsale.R
import com.emmsale.databinding.ItemEventInformationImageBinding
import com.stfalcon.imageviewer.StfalconImageViewer

class EventInfoImageViewHolder(
    private val binding: ItemEventInformationImageBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(detailImageUrl: String) {
        with(binding.ivDetailImage) {
            Glide.with(this.context)
                .load(detailImageUrl)
                .placeholder(R.drawable.img_all_loading)
                .error(R.mipmap.ic_launcher)
                .fallback(R.mipmap.ic_launcher)
                .into(binding.ivDetailImage)

            setOnClickListener {
                StfalconImageViewer.Builder(this.context, listOf(detailImageUrl)) { view, image ->
                    Glide.with(this.context)
                        .load(image)
                        .placeholder(R.drawable.img_all_loading)
                        .error(R.mipmap.ic_launcher)
                        .into(view)
                }.show()
            }
        }
    }

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

    companion object {
        fun from(parent: ViewGroup): EventInfoImageViewHolder {
            val binding = ItemEventInformationImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
            return EventInfoImageViewHolder(binding)
        }
    }
}
