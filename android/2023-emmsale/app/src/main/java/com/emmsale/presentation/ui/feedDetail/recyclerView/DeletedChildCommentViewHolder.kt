package com.emmsale.presentation.ui.feedDetail.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R

class DeletedChildCommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun from(parent: ViewGroup): DeletedChildCommentViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val itemView =
                layoutInflater.inflate(R.layout.item_all_deleted_child_comment, parent, false)
            return DeletedChildCommentViewHolder(itemView)
        }
    }
}
