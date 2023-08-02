package com.emmsale.presentation.ui.main.event.conferenceFilter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEachIndexed
import com.emmsale.R
import com.emmsale.databinding.ActivityConferenceFilterBinding
import com.emmsale.databinding.LayoutFilterConferenceDurationBinding
import com.emmsale.databinding.LayoutFilterConferenceEventTagBinding
import com.emmsale.databinding.LayoutFilterConferenceStatusBinding
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.views.activityChipOf
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilterDateUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilterTagUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilterUiState

class ConferenceFilterActivity : AppCompatActivity() {
    private val viewModel: ConferenceFilterViewModel by viewModels { ConferenceFilterViewModel.factory }
    private val binding: ActivityConferenceFilterBinding by lazy {
        ActivityConferenceFilterBinding.inflate(layoutInflater)
    }
    private val eventStatusBinding: LayoutFilterConferenceStatusBinding by lazy { binding.layoutFilterStatus }
    private val eventTagBinding: LayoutFilterConferenceEventTagBinding by lazy { binding.layoutFilterTag }
    private val eventDurationBinding: LayoutFilterConferenceDurationBinding by lazy { binding.layoutFilterDuration }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupEventFilters()

        eventDurationBinding.btnFilterEndDuration.setOnClickListener {
            showToast("End duration clicked")
        }

        eventDurationBinding.btnFilterStartDuration.setOnClickListener {
            showToast("Start duration clicked")
        }
    }

    private fun setupEventFilters() {
        viewModel.eventFilters.observe(this) { eventFilters ->
            when (eventFilters) {
                is ConferenceFilterUiState.Success -> {
                    updateFilterViews(eventFilters)
                    binding.progressbarLoading.visibility = View.GONE
                }

                is ConferenceFilterUiState.Error -> {
                    showToast(getString(R.string.all_data_loading_failed_message))
                    binding.progressbarLoading.visibility = View.GONE
                }

                is ConferenceFilterUiState.Loading ->
                    binding.progressbarLoading.visibility = View.VISIBLE
            }
        }
    }

    private fun updateFilterViews(eventFilters: ConferenceFilterUiState.Success) {
        updateConferenceTags(eventFilters.tags)
        updateConferenceDurations(eventFilters.selectedStartDate, eventFilters.selectedEndDate)
    }

    private fun updateConferenceTags(eventTags: List<ConferenceFilterTagUiState>) {
        removeFilterTagsExcludingAllTag()
        eventTags.forEach(::addFilterTag)
    }

    private fun removeFilterTagsExcludingAllTag() {
        eventTagBinding.cgConferenceTagTags.forEachIndexed { index, view ->
            if (index == 0) return@forEachIndexed
            eventTagBinding.cgConferenceTagTags.removeView(view)
        }
    }

    private fun addFilterTag(tag: ConferenceFilterTagUiState) {
        eventTagBinding.cgConferenceTagTags.addView(
            activityChipOf { text = tag.name }
        )
    }

    private fun updateConferenceDurations(
        startDate: ConferenceFilterDateUiState,
        endDate: ConferenceFilterDateUiState,
    ) {
        eventDurationBinding.btnFilterStartDuration.text = transformConferenceDate(startDate)
        eventDurationBinding.btnFilterEndDuration.text = transformConferenceDate(endDate)
    }

    private fun transformConferenceDate(conferenceDate: ConferenceFilterDateUiState): String =
        getString(
            R.string.event_filter_duration_date_format,
            conferenceDate.year, conferenceDate.month
        )

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, ConferenceFilterActivity::class.java))
        }
    }
}
