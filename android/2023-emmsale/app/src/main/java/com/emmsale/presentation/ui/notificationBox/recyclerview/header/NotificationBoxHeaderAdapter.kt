package com.emmsale.presentation.ui.notificationBox.recyclerview.header

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.notificationBox.recyclerview.body.NotificationBodyClickListener
import com.emmsale.presentation.ui.notificationBox.uistate.NotificationHeaderUiState

class NotificationBoxHeaderAdapter(
    private val notificationHeaderClickListener: NotificationHeaderClickListener,
    private val notificationBodyClickListener: NotificationBodyClickListener,
) : ListAdapter<NotificationHeaderUiState, NotificationHeaderViewHolder>(NotificationHeaderDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NotificationHeaderViewHolder = NotificationHeaderViewHolder(
        parent,
        notificationHeaderClickListener,
        notificationBodyClickListener
    )

    override fun onBindViewHolder(holder: NotificationHeaderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
