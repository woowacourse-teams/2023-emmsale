package com.emmsale.presentation.ui.onboarding

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.emmsale.R
import com.emmsale.presentation.ui.onboarding.uistate.CareerContentUiState

class CareerSpinnerAdapter(
    private val careerContents: MutableList<CareerContentUiState>
) : BaseAdapter() {

    override fun getCount(): Int = careerContents.size

    override fun getItem(position: Int): CareerContentUiState = careerContents[position]

    override fun getItemId(position: Int): Long = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = convertView
        if (view == null) view =
            View.inflate(parent?.context, R.layout.item_history_spinner_tag, null)

        val careerTagViewHolder = (view!!.tag as CareerTagViewHolder?)
            ?: CareerTagViewHolder(view).also { view.tag = it }

        careerTagViewHolder.bind(careerContents[position])
        return view
    }

    fun updateItems(newCareerTags: List<CareerContentUiState>) {
        careerContents.clear()
        careerContents.addAll(newCareerTags)
        notifyDataSetChanged()
    }
}
