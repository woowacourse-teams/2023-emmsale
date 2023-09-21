package com.emmsale.presentation.ui.messageList.recyclerview.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.emmsale.presentation.ui.messageList.uistate.MessageUiState

abstract class MessageViewHolder(view: View) : ViewHolder(view) {
    abstract fun bind(message: MessageUiState)
}
