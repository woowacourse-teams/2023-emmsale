package com.emmsale.presentation.ui.messageRoomList.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.data.model.MessageRoom

class MessageRoomListAdapter(
    private val onMessageRoomClick: (roomId: String, otherUid: Long) -> Unit,
) : ListAdapter<MessageRoom, MessageRoomViewHolder>(MessageRoomDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageRoomViewHolder {
        return MessageRoomViewHolder(parent, onMessageRoomClick)
    }

    override fun onBindViewHolder(holder: MessageRoomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
