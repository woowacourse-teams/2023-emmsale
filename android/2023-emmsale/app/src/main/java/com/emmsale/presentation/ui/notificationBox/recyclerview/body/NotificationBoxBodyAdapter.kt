package com.emmsale.presentation.ui.notificationBox.recyclerview.body

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.notificationBox.uistate.NotificationBodyUiState

class NotificationBoxBodyAdapter(
    private val notificationBodyClickListener: NotificationBodyClickListener,
) : ListAdapter<NotificationBodyUiState, NotificationBodyViewHolder>(NotificationBodyDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationBodyViewHolder =
        NotificationBodyViewHolder(parent, notificationBodyClickListener)

    override fun onBindViewHolder(holder: NotificationBodyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
