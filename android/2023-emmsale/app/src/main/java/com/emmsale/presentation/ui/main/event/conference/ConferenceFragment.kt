package com.emmsale.presentation.ui.main.event.conference

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentConferenceBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.common.extension.getParcelableExtraCompat
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.views.FilterTag
import com.emmsale.presentation.common.views.filterChipOf
import com.emmsale.presentation.eventdetail.EventDetailActivity
import com.emmsale.presentation.ui.main.event.conference.uistate.ConferencesUiState
import com.emmsale.presentation.ui.main.event.conference.uistate.EventsUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.ConferenceFilterActivity
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilterDateUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilterUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFiltersUiState

class ConferenceFragment : BaseFragment<FragmentConferenceBinding>() {
    override val layoutResId: Int = R.layout.fragment_conference
    private val viewModel: ConferenceViewModel by viewModels { ConferenceViewModel.factory }
    private val eventAdapter: ConferenceRecyclerViewAdapter by lazy {
        ConferenceRecyclerViewAdapter(::navigateToEventDetail)
    }
    private val filterActivityLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result == null || result.resultCode != RESULT_OK) return@registerForActivityResult
            val filters: ConferenceFiltersUiState.Success = result.data?.getParcelableExtraCompat(
                ConferenceFilterActivity.FILTERS_KEY,
            ) ?: return@registerForActivityResult

            viewModel.updateConferenceFilter(filters)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setupEventsObserver()
        setupFiltersObserver()
    }

    private fun initView() {
        initViewModel()
        initEventRecyclerView()
        initClickListener()
    }

    private fun initViewModel() {
        binding.viewModel = viewModel
    }

    private fun initEventRecyclerView() {
        binding.rvEvents.adapter = eventAdapter
        binding.rvEvents.addItemDecoration(ConferenceRecyclerViewDivider(requireContext()))
    }

    private fun setupEventsObserver() {
        viewModel.events.observe(viewLifecycleOwner) { eventsResult ->
            when (eventsResult) {
                is EventsUiState.Success -> {
                    binding.progressbarLoading.visibility = View.GONE
                    eventAdapter.submitList(eventsResult.events)
                    binding.tvEventsCount.text =
                        getString(R.string.event_count_format, eventsResult.eventSize)
                }

                is EventsUiState.Loading -> binding.progressbarLoading.visibility = View.VISIBLE
                is EventsUiState.Error -> {
                    binding.progressbarLoading.visibility = View.GONE
                    requireContext().showToast(getString(R.string.all_data_loading_failed_message))
                }
            }
        }
    }

    private fun setupFiltersObserver() {
        viewModel.selectedFilters.observe(viewLifecycleOwner) { filters ->
            clearFilterViews()
            addFilterViews(filters.tags + filters.statuses)
            addDurationFilter(filters.selectedStartDate, filters.selectedEndDate)
        }
    }

    private fun clearFilterViews() {
        binding.layoutConferenceFilters.removeAllViews()
    }

    private fun addFilterViews(filters: List<ConferenceFilterUiState>) {
        filters
            .filter { it.isSelected }
            .forEach { binding.layoutConferenceFilters.addView(createFilterTag(it.name)) }
    }

    private fun addDurationFilter(
        startDate: ConferenceFilterDateUiState?,
        endDate: ConferenceFilterDateUiState?,
    ) {
        val startDateString = startDate?.transformToDateString(requireContext())
        val endDateString = endDate?.transformToDateString(requireContext(), true) ?: ""
        val durationString = "$startDateString$endDateString"

        if (startDateString != null) {
            binding.layoutConferenceFilters.addView(createFilterTag(durationString))
        }
    }

    private fun createFilterTag(title: String): FilterTag = filterChipOf {
        text = title
    }

    private fun initClickListener() {
        binding.btnEventFilter.setOnClickListener { navigateToEventFilter() }
    }

    private fun navigateToEventDetail(event: ConferencesUiState) {
        EventDetailActivity.startActivity(requireContext(), event.id)
    }

    private fun navigateToEventFilter() {
        val filterActivityIntent = ConferenceFilterActivity.createIntent(
            requireContext(),
            viewModel.selectedFilters.value,
        )
        filterActivityLauncher.launch(filterActivityIntent)
    }
}
