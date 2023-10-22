package com.emmsale.presentation.ui.messageList.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.emmsale.R
import com.emmsale.databinding.ItemMyMessageBinding
import com.emmsale.presentation.ui.messageList.uistate.MessageUiState
import com.emmsale.presentation.ui.messageList.uistate.MyMessageUiState

class MyMessageViewHolder(
    parent: ViewGroup,
    onBackgroundClick: () -> Unit,
) : MessageViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_my_message, parent, false),
) {
    val binding = ItemMyMessageBinding.bind(itemView)

    init {
        binding.root.setOnClickListener { onBackgroundClick() }
    }

    override fun bind(message: MessageUiState) {
        if (message !is MyMessageUiState) return
        binding.message = message
    }
}
