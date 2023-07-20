package com.emmsale.presentation.ui.onboarding

import android.view.View
import android.widget.TextView
import com.emmsale.R
import com.emmsale.presentation.ui.onboarding.uistate.CareerContentUiState

class CareerTagViewHolder(view: View) {
    private val careerContentTv: TextView = view.findViewById(R.id.tv_career_content)

    fun bind(careerContent: CareerContentUiState) {
        careerContentTv.text = careerContent.name
    }
}
