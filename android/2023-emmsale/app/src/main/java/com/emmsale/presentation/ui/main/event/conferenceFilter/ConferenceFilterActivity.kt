package com.emmsale.presentation.ui.main.event.conferenceFilter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEachIndexed
import com.emmsale.R
import com.emmsale.databinding.ActivityConferenceFilterBinding
import com.emmsale.databinding.LayoutFilterConferenceDurationBinding
import com.emmsale.databinding.LayoutFilterConferenceEventTagBinding
import com.emmsale.databinding.LayoutFilterConferenceStatusBinding
import com.emmsale.presentation.common.extension.getParcelableExtraCompat
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.views.activityChipOf
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilterDateUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilterUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFiltersUiState
import com.google.android.material.chip.ChipGroup

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
        initView()
        initBackPressedDispatcher()
        setupEventFilters()
        fetchFilters()
    }

    private fun initView() {
        initEventFilterToolbarNavClickListener()
        initEventFilterApplyButtonClickListener()
    }

    private fun initEventFilterToolbarNavClickListener() {
        binding.tbEventFilter.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun initEventFilterApplyButtonClickListener() {
        binding.btnFilterApply.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun initBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(this, ConferenceFilterOnBackPressedCallback())
    }

    private fun setupEventFilters() {
        viewModel.eventFilters.observe(this) { eventFilters ->
            when (eventFilters) {
                is ConferenceFiltersUiState.Success -> {
                    updateFilterViews(eventFilters)
                    binding.progressbarLoading.visibility = View.GONE
                }

                is ConferenceFiltersUiState.Error -> {
                    showToast(getString(R.string.all_data_loading_failed_message))
                    binding.progressbarLoading.visibility = View.GONE
                }

                is ConferenceFiltersUiState.Loading ->
                    binding.progressbarLoading.visibility = View.VISIBLE
            }
        }
    }

    private fun updateFilterViews(eventFilters: ConferenceFiltersUiState.Success) {
        updateConferenceStatus(eventFilters.statuses)
        updateConferenceTags(eventFilters.tags)
        updateConferenceDurations(eventFilters.selectedStartDate, eventFilters.selectedEndDate)
    }

    private fun updateConferenceStatus(eventStatuses: List<ConferenceFilterUiState>) {
        removeFilterStatuses()
        eventStatuses.forEach {
            addTagFilter(eventStatusBinding.cgConferenceStatusTags, it)
        }
    }

    private fun updateConferenceTags(eventTags: List<ConferenceFilterUiState>) {
        removeFilterTagsExcludingAllTag()
        eventTags.forEach {
            addTagFilter(eventTagBinding.cgConferenceTagTags, it)
        }
    }

    private fun removeFilterStatuses() {
        eventStatusBinding.cgConferenceStatusTags.forEachIndexed { _, view ->
            eventStatusBinding.cgConferenceStatusTags.removeView(view)
        }
    }

    private fun removeFilterTagsExcludingAllTag() {
        eventTagBinding.cgConferenceTagTags.forEachIndexed { index, view ->
            if (index == 0) return@forEachIndexed
            eventTagBinding.cgConferenceTagTags.removeView(view)
        }
    }

    private fun addTagFilter(chipGroup: ChipGroup, tag: ConferenceFilterUiState) {
        chipGroup.addView(
            activityChipOf {
                text = tag.name
                isChecked = tag.isSelected
                setOnCheckedChangeListener { _, _ -> viewModel.toggleFilterSelection(tag) }
            }
        )
    }

    private fun updateConferenceDurations(
        startDate: ConferenceFilterDateUiState?,
        endDate: ConferenceFilterDateUiState?,
    ) {
        eventDurationBinding.btnFilterStartDuration.text = transformConferenceDate(startDate)
        eventDurationBinding.btnFilterEndDuration.text = transformConferenceDate(endDate)
    }

    private fun transformConferenceDate(conferenceDate: ConferenceFilterDateUiState?): String =
        getString(
            R.string.event_filter_duration_date_format,
            conferenceDate?.year, conferenceDate?.month
        )

    private fun fetchFilters() {
        viewModel.updateFilters(intent.getParcelableExtraCompat(FILTERS_KEY))
    }

    companion object {
        const val FILTERS_KEY = "filters_key"

        fun createIntent(
            context: Context,
            selectedFilters: ConferenceFiltersUiState.Success?,
        ): Intent = Intent(context, ConferenceFilterActivity::class.java)
            .putExtra(FILTERS_KEY, selectedFilters)
    }

    inner class ConferenceFilterOnBackPressedCallback : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val filters = viewModel.eventFilters.value
            if (filters is ConferenceFiltersUiState.Success) {
                val intent = Intent()
                intent.putExtra(FILTERS_KEY, filters)
                setResult(RESULT_OK, intent)
            }
            finish()
        }
    }
}
