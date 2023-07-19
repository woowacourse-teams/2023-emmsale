package com.emmsale.presentation.ui.onboarding

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.emmsale.R
import com.emmsale.presentation.ui.onboarding.model.EducationUiState

class EducationAdapter(
    private val educations: MutableList<EducationUiState>
) : BaseAdapter() {

    override fun getCount(): Int = educations.size

    override fun getItem(position: Int): EducationUiState = educations[position]

    override fun getItemId(position: Int): Long = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = convertView
        if (view == null) view =
            View.inflate(parent?.context, R.layout.item_education_history, null)

        val educationViewHolder =
            (view!!.tag as EducationViewHolder?) ?: EducationViewHolder(view).also { view.tag = it }

        educationViewHolder.bind(educations[position])
        return view
    }

    fun updateItems(newEducations: List<EducationUiState>) {
        educations.clear()
        educations.addAll(newEducations)
        notifyDataSetChanged()
    }
}
