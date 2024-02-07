package com.emmsale.presentation.ui.conferenceList.recyclerView

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader.PreloadModelProvider
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.emmsale.data.model.Event
import com.emmsale.presentation.common.extension.dp

class EventRecyclerViewAdapter(
    private val fragment: Fragment,
    private val onClickConference: (Event) -> Unit,
    private val onPreloaderReady: (preloader: RecyclerViewPreloader<Event>) -> Unit,
) : ListAdapter<Event, EventViewHolder>(EventDiffUtil), PreloadModelProvider<Event> {

    private var isFirstPreloader: Boolean = true

    private val requestManager: RequestManager = Glide.with(fragment)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): EventViewHolder = EventViewHolder(
        parent = parent,
        onClickConference = onClickConference,
        onEventPosterPreDraw = { view ->
            if (isFirstPreloader) {
                isFirstPreloader = false
                val preloader = RecyclerViewPreloader(
                    fragment,
                    this,
                    ViewPreloadSizeProvider(view),
                    MAX_PRELOAD,
                )
                onPreloaderReady(preloader)
            }
        },
    )

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getPreloadItems(position: Int): MutableList<Event> =
        mutableListOf(getItem(position))

    override fun getPreloadRequestBuilder(event: Event): RequestBuilder<Drawable> = requestManager
        .load(event.posterImageUrl)
        .transform(CenterCrop(), RoundedCorners(15.dp))

    companion object {
        private const val MAX_PRELOAD = 8
    }
}
