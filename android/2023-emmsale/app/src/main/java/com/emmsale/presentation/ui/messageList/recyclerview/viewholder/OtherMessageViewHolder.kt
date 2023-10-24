package com.emmsale.presentation.ui.messageList.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.emmsale.R
import com.emmsale.databinding.ItemOtherMessageBinding
import com.emmsale.presentation.ui.messageList.uistate.MessageUiState
import com.emmsale.presentation.ui.messageList.uistate.OtherMessageUiState

class OtherMessageViewHolder(
    parent: ViewGroup,
    onProfileClick: (uid: Long) -> Unit,
) : MessageViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_other_message, parent, false),
) {
    val binding = ItemOtherMessageBinding.bind(itemView)

    init {
        binding.onProfileClick = onProfileClick
    }

    override fun bind(message: MessageUiState) {
        if (message !is OtherMessageUiState) return
        binding.message = message
    }
}
