package com.emmsale.presentation.ui.onboarding

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.emmsale.R
import com.emmsale.presentation.ui.onboarding.uistate.ResumeTagUiState

class ResumeTagSpinnerAdapter(
    private val tags: MutableList<ResumeTagUiState>
) : BaseAdapter() {

    override fun getCount(): Int = tags.size

    override fun getItem(position: Int): ResumeTagUiState = tags[position]

    override fun getItemId(position: Int): Long = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = convertView
        if (view == null) view =
            View.inflate(parent?.context, R.layout.item_history_spinner_tag, null)

        val resumeTagViewHolder =
            (view!!.tag as ResumeTagViewHolder?) ?: ResumeTagViewHolder(view).also {
                view.tag = it
            }

        resumeTagViewHolder.bind(tags[position])
        return view
    }

    fun updateItems(newEducationTags: List<ResumeTagUiState>) {
        tags.clear()
        tags.addAll(newEducationTags)
        notifyDataSetChanged()
    }
}
