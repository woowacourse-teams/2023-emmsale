package com.emmsale.presentation.ui.messageRoomList.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.emmsale.R
import com.emmsale.data.messageRoom.MessageRoom
import com.emmsale.databinding.ItemMessageRoomBinding

class MessageRoomViewHolder(
    parent: ViewGroup,
    onMessageRoomClick: (roomId: String, otherUid: Long) -> Unit,
) : ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_message_room, parent, false),
) {
    private val binding = ItemMessageRoomBinding.bind(itemView)

    init {
        binding.onMessageRoomClick = onMessageRoomClick
    }

    fun bind(messageRoom: MessageRoom) {
        binding.messageRoom = messageRoom
    }
}
