package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemPrimarynotificationChildCommentNotificationBinding
import com.emmsale.databinding.ItemPrimarynotificationInterestEventNotificationBinding
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.ChildCommentNotificationUiState1
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.InterestEventNotificationUiState1
import com.emmsale.presentation.ui.notificationBox.primaryNotification.uistate.PrimaryNotificationUiState1

class PrimaryNotificationsAdapter(
    private val readNotification: (notificationId: Long) -> Unit,
    private val showEvent: (eventId: Long) -> Unit,
    private val showChildComments: (eventId: Long, parentCommentId: Long) -> Unit,
    private val deleteNotification: (notificationId: Long) -> Unit,
) : ListAdapter<PrimaryNotificationUiState1, PrimaryNotificationViewHolder>(diffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PrimaryNotificationViewHolder =
        when (viewType) {
            PrimaryNotificationViewType1.CHILD_COMMENT.ordinal -> ChildCommentNotificationViewHolder.create(
                parent = parent,
                readNotification = readNotification,
                deleteNotification = deleteNotification,
                showChildComments = showChildComments,
            )

            PrimaryNotificationViewType1.INTEREST_EVENT.ordinal -> InterestEventNotificationViewHolder.create(
                parent = parent,
                readNotification = readNotification,
                deleteNotification = deleteNotification,
                showEvent = showEvent,
            )

            else -> throw IllegalArgumentException("${PrimaryNotificationViewType1::class.simpleName}에 속하지 않은 뷰 타입입니다.")
        }

    override fun onBindViewHolder(holder: PrimaryNotificationViewHolder, position: Int) {
        when (holder) {
            is ChildCommentNotificationViewHolder -> holder.bind(getItem(position) as ChildCommentNotificationUiState1)
            is InterestEventNotificationViewHolder -> holder.bind(getItem(position) as InterestEventNotificationUiState1)
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is ChildCommentNotificationUiState1 -> PrimaryNotificationViewType1.CHILD_COMMENT.ordinal
            is InterestEventNotificationUiState1 -> PrimaryNotificationViewType1.INTEREST_EVENT.ordinal
            else -> throw IllegalArgumentException("${PrimaryNotificationUiState1::class.simpleName}에 속하지 않은 UiState입니다.")
        }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<PrimaryNotificationUiState1>() {
            override fun areItemsTheSame(
                oldItem: PrimaryNotificationUiState1,
                newItem: PrimaryNotificationUiState1,
            ): Boolean = oldItem.notificationId == newItem.notificationId

            override fun areContentsTheSame(
                oldItem: PrimaryNotificationUiState1,
                newItem: PrimaryNotificationUiState1,
            ): Boolean = oldItem == newItem
        }
    }
}

sealed class PrimaryNotificationViewHolder(
    parent: ViewGroup,
) : RecyclerView.ViewHolder(parent)

class ChildCommentNotificationViewHolder(
    private val binding: ItemPrimarynotificationChildCommentNotificationBinding,
    private val readNotification: (notificationId: Long) -> Unit,
    private val showChildComments: (eventId: Long, parentCommentId: Long) -> Unit,
    private val deleteNotification: (notificationId: Long) -> Unit,
) : PrimaryNotificationViewHolder(binding.root as ViewGroup) {

    init {
        binding.root.setOnClickListener {
            readNotification(binding.notification?.notificationId ?: return@setOnClickListener)
            showChildComments(
                binding.notification?.eventId ?: return@setOnClickListener,
                binding.notification?.parentCommentId ?: return@setOnClickListener,
            )
        }
        binding.ivChildcommentnotificationDeleteButton.setOnClickListener {
            deleteNotification(binding.notification?.notificationId ?: return@setOnClickListener)
        }
    }

    fun bind(notification: ChildCommentNotificationUiState1) {
        binding.notification = notification
    }

    companion object {
        fun create(
            parent: ViewGroup,
            readNotification: (notificationId: Long) -> Unit,
            showChildComments: (eventId: Long, parentCommentId: Long) -> Unit,
            deleteNotification: (notificationId: Long) -> Unit,
        ): ChildCommentNotificationViewHolder {
            val binding = ItemPrimarynotificationChildCommentNotificationBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

            return ChildCommentNotificationViewHolder(
                binding = binding,
                readNotification = readNotification,
                showChildComments = showChildComments,
                deleteNotification = deleteNotification,
            )
        }
    }
}

class InterestEventNotificationViewHolder(
    private val binding: ItemPrimarynotificationInterestEventNotificationBinding,
    private val readNotification: (notificationId: Long) -> Unit,
    private val showEvent: (eventId: Long) -> Unit,
    private val deleteNotification: (notificationId: Long) -> Unit,
) : PrimaryNotificationViewHolder(binding.root as ViewGroup) {

    init {
        binding.root.setOnClickListener {
            readNotification(binding.notification?.notificationId ?: return@setOnClickListener)
            showEvent(binding.notification?.eventId ?: return@setOnClickListener)
        }
        binding.ivInteresteventnotificationDeleteButton.setOnClickListener {
            deleteNotification(binding.notification?.notificationId ?: return@setOnClickListener)
        }
    }

    fun bind(notification: InterestEventNotificationUiState1) {
        binding.notification = notification
    }

    companion object {
        fun create(
            parent: ViewGroup,
            readNotification: (notificationId: Long) -> Unit,
            showEvent: (eventId: Long) -> Unit,
            deleteNotification: (notificationId: Long) -> Unit,
        ): InterestEventNotificationViewHolder {
            val binding = ItemPrimarynotificationInterestEventNotificationBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

            return InterestEventNotificationViewHolder(
                binding = binding,
                readNotification = readNotification,
                showEvent = showEvent,
                deleteNotification = deleteNotification,
            )
        }
    }
}

enum class PrimaryNotificationViewType1 {
    INTEREST_EVENT, CHILD_COMMENT
}
