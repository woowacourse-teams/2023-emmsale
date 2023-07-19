package com.emmsale.presentation.ui.onboarding

import android.view.View
import android.widget.TextView
import com.emmsale.R
import com.emmsale.presentation.ui.onboarding.model.EducationUiState

class EducationViewHolder(view: View) {
    private val educationTitleTextView: TextView = view.findViewById(R.id.tv_education_title)

    fun bind(education: EducationUiState) {
        educationTitleTextView.text = education.name
    }
}
