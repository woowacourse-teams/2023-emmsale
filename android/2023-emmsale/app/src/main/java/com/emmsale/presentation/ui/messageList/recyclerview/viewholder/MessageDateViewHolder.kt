package com.emmsale.presentation.ui.messageList.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.emmsale.R
import com.emmsale.databinding.ItemMessageDateBinding
import com.emmsale.presentation.ui.messageList.uistate.MessageDateUiState
import com.emmsale.presentation.ui.messageList.uistate.MessageUiState

class MessageDateViewHolder(
    parent: ViewGroup,
) : MessageViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_message_date, parent, false),
) {
    private val binding = ItemMessageDateBinding.bind(itemView)

    override fun bind(message: MessageUiState) {
        if (message !is MessageDateUiState) return
        binding.message = message
    }
}
