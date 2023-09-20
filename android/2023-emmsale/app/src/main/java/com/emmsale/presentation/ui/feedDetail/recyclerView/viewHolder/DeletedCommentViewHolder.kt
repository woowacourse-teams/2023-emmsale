package com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R

class DeletedCommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun from(parent: ViewGroup): DeletedCommentViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val itemView = layoutInflater.inflate(R.layout.item_all_deleted_comment, parent, false)
            return DeletedCommentViewHolder(itemView)
        }
    }
}
