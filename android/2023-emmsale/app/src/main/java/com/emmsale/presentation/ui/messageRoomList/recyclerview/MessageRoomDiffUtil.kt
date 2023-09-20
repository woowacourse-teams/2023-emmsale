package com.emmsale.presentation.ui.messageRoomList.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.data.messageRoom.MessageRoom

object MessageRoomDiffUtil : DiffUtil.ItemCallback<MessageRoom>() {
    override fun areItemsTheSame(
        oldItem: MessageRoom,
        newItem: MessageRoom,
    ): Boolean = oldItem.roomId == newItem.roomId

    override fun areContentsTheSame(
        oldItem: MessageRoom,
        newItem: MessageRoom,
    ): Boolean = oldItem == newItem
}
