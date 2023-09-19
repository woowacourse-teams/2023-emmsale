package com.emmsale.presentation.ui.messageRoomList.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.data.messageRoom.MessageRoom

class MessageRoomListAdapter :
    ListAdapter<MessageRoom, MessageRoomViewHolder>(MessageRoomDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageRoomViewHolder {
        return MessageRoomViewHolder(parent)
    }

    override fun onBindViewHolder(holder: MessageRoomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
