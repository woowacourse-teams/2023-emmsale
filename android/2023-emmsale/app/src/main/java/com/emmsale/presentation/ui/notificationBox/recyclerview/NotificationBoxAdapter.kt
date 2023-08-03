package com.emmsale.presentation.ui.notificationBox.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.notificationBox.uistate.NotificationUiState

class NotificationBoxAdapter(
    private val onClickNotification: (notificationId: Long, otherUid: Long) -> Unit,
    private val onDelete: (notificationId: Long) -> Unit,
) : ListAdapter<NotificationUiState, NotificationViewHolder>(NotificationDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder =
        NotificationViewHolder(parent, onClickNotification, onDelete)

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

