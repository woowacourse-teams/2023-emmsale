package com.emmsale.presentation.ui.onboarding

import android.view.View
import android.widget.TextView
import com.emmsale.R
import com.emmsale.presentation.ui.onboarding.uistate.ResumeTagUiState

class ResumeTagViewHolder(view: View) {
    private val titleTextView: TextView = view.findViewById(R.id.tv_tag_title)

    fun bind(tag: ResumeTagUiState) {
        titleTextView.text = tag.name
    }
}
