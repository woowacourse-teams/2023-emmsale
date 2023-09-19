package com.emmsale.presentation.ui.messageList.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.messageList.recyclerview.viewholder.MessageDateViewHolder
import com.emmsale.presentation.ui.messageList.recyclerview.viewholder.MessageViewHolder
import com.emmsale.presentation.ui.messageList.recyclerview.viewholder.MyMessageViewHolder
import com.emmsale.presentation.ui.messageList.recyclerview.viewholder.OtherMessageViewHolder
import com.emmsale.presentation.ui.messageList.uistate.MessageUiState

class MessageListAdapter : ListAdapter<MessageUiState, MessageViewHolder>(MessageDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when (viewType) {
            MessageUiState.MessageType.MY.ordinal -> MyMessageViewHolder(parent)
            MessageUiState.MessageType.OTHER.ordinal -> OtherMessageViewHolder(parent)
            MessageUiState.MessageType.DATE.ordinal -> MessageDateViewHolder(parent)
            else -> throw IllegalArgumentException(INVALID_VIEW_TYPE_ERROR)
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int = getItem(position).messageType.ordinal

    companion object {
        private const val INVALID_VIEW_TYPE_ERROR = "[ERROR] 올바르지 않은 ViewType 입니다."
    }
}
