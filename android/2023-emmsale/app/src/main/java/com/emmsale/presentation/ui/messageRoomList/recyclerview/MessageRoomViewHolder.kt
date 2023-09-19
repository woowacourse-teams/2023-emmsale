package com.emmsale.presentation.ui.messageRoomList.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.emmsale.R
import com.emmsale.data.messageRoom.MessageRoom
import com.emmsale.databinding.ItemMessageRoomBinding

class MessageRoomViewHolder(
    parent: ViewGroup,
) : ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_message_room, parent, false),
) {
    private val binding = ItemMessageRoomBinding.bind(itemView)

    fun bind(messageRoom: MessageRoom) {
        binding.messageRoom = messageRoom
    }
}
