package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.PrimaryNotificationViewType
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewHolder.ChildCommentNotificationViewHolder
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewHolder.InterestEventNotificationViewHolder
import com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewHolder.PrimaryNotificationViewHolder
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.ChildCommentNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.InterestEventNotificationUiState
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationUiState

class PrimaryNotificationsAdapter(
    private val readNotification: (notificationId: Long) -> Unit,
    private val showEvent: (eventId: Long) -> Unit,
    private val showChildComments: (eventId: Long, parentCommentId: Long) -> Unit,
    private val deleteNotification: (notificationId: Long) -> Unit,
) : ListAdapter<PrimaryNotificationUiState, PrimaryNotificationViewHolder>(diffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PrimaryNotificationViewHolder =
        when (viewType) {
            PrimaryNotificationViewType.CHILD_COMMENT.ordinal -> ChildCommentNotificationViewHolder.create(
                parent = parent,
                readNotification = readNotification,
                deleteNotification = deleteNotification,
                showChildComments = showChildComments,
            )

            PrimaryNotificationViewType.INTEREST_EVENT.ordinal -> InterestEventNotificationViewHolder.create(
                parent = parent,
                readNotification = readNotification,
                deleteNotification = deleteNotification,
                showEvent = showEvent,
            )

            else -> throw IllegalArgumentException("${PrimaryNotificationViewType::class.simpleName}에 속하지 않은 뷰 타입입니다.")
        }

    override fun onBindViewHolder(holder: PrimaryNotificationViewHolder, position: Int) {
        when (holder) {
            is ChildCommentNotificationViewHolder -> holder.bind(getItem(position) as ChildCommentNotificationUiState)
            is InterestEventNotificationViewHolder -> holder.bind(getItem(position) as InterestEventNotificationUiState)
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is ChildCommentNotificationUiState -> PrimaryNotificationViewType.CHILD_COMMENT.ordinal
            is InterestEventNotificationUiState -> PrimaryNotificationViewType.INTEREST_EVENT.ordinal
            else -> throw IllegalArgumentException("${PrimaryNotificationUiState::class.simpleName}에 속하지 않은 UiState입니다.")
        }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<PrimaryNotificationUiState>() {
            override fun areItemsTheSame(
                oldItem: PrimaryNotificationUiState,
                newItem: PrimaryNotificationUiState,
            ): Boolean = oldItem.notificationId == newItem.notificationId

            override fun areContentsTheSame(
                oldItem: PrimaryNotificationUiState,
                newItem: PrimaryNotificationUiState,
            ): Boolean = oldItem == newItem
        }
    }
}
