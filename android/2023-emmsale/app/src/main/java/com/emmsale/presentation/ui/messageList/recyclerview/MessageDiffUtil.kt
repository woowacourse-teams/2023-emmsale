package com.emmsale.presentation.ui.messageList.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.messageList.uistate.MessageUiState

object MessageDiffUtil : DiffUtil.ItemCallback<MessageUiState>() {
    override fun areItemsTheSame(
        oldItem: MessageUiState,
        newItem: MessageUiState,
    ): Boolean = oldItem.createdAt == newItem.createdAt

    override fun areContentsTheSame(
        oldItem: MessageUiState,
        newItem: MessageUiState,
    ): Boolean = oldItem == newItem
}
