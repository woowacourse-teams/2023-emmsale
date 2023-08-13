package com.emmsale.presentation.ui.main.event.conference

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentConferenceBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.extension.getSerializableExtraCompat
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.views.FilterTag
import com.emmsale.presentation.common.views.filterChipOf
import com.emmsale.presentation.ui.eventdetail.EventDetailActivity
import com.emmsale.presentation.ui.main.event.conference.recyclerview.ConferenceRecyclerViewAdapter
import com.emmsale.presentation.ui.main.event.conference.recyclerview.ConferenceRecyclerViewDivider
import com.emmsale.presentation.ui.main.event.conference.uistate.ConferenceItemUiState
import com.emmsale.presentation.ui.main.event.conference.uistate.ConferenceSelectedFilteringDateOptionUiState
import com.emmsale.presentation.ui.main.event.conference.uistate.ConferenceSelectedFilteringOptionUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.ConferenceFilterActivity
import java.time.LocalDate

class ConferenceFragment : BaseFragment<FragmentConferenceBinding>() {
    override val layoutResId: Int = R.layout.fragment_conference
    private val viewModel: ConferenceViewModel by viewModels { ConferenceViewModel.factory }
    private val eventAdapter: ConferenceRecyclerViewAdapter by lazy {
        ConferenceRecyclerViewAdapter(::navigateToEventDetail)
    }
    private val filterActivityLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result == null || result.resultCode != RESULT_OK) return@registerForActivityResult

            val selectedStatusFilterIds = result.data
                ?.getLongArrayExtra(ConferenceFilterActivity.KEY_SELECTED_CONFERENCE_STATUS_FILTER_IDS)
                ?.toTypedArray() ?: emptyArray()
            val selectedTagFilterIds: Array<Long> = result.data
                ?.getLongArrayExtra(ConferenceFilterActivity.KEY_SELECTED_CONFERENCE_TAG_FILTER_IDS)
                ?.toTypedArray() ?: emptyArray()
            val selectedStartDate =
                result.data?.getSerializableExtraCompat<LocalDate>(ConferenceFilterActivity.KEY_SELECTED_START_DATE)
            val selectedEndDate =
                result.data?.getSerializableExtraCompat<LocalDate>(ConferenceFilterActivity.KEY_SELECTED_END_DATE)

            viewModel.fetchFilteredConferences(
                selectedStatusFilterIds,
                selectedTagFilterIds,
                selectedStartDate,
                selectedEndDate,
            )
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
        initEventFilterButtonClickListener()
    }

    private fun initViewModel() {
        binding.viewModel = viewModel
    }

    private fun initEventRecyclerView() {
        binding.rvEvents.adapter = eventAdapter
        binding.rvEvents.addItemDecoration(ConferenceRecyclerViewDivider(requireContext()))
    }

    private fun setupEventsObserver() {
        viewModel.conference.observe(viewLifecycleOwner) { eventsResult ->
            when {
                eventsResult.isLoadingConferencesFailed -> requireContext().showToast(getString(R.string.all_data_loading_failed_message))
                !eventsResult.isLoading -> eventAdapter.submitList(eventsResult.conferenceItems) {
                    binding.rvEvents.scrollToPosition(0)
                }
            }
        }
    }

    private fun setupFiltersObserver() {
        viewModel.selectedFilter.observe(viewLifecycleOwner) { filters ->
            clearFilterViews()
            addFilterViews(filters.conferenceTagFilteringOptions + filters.conferenceStatusFilteringOptions)
            addDurationFilter(filters.selectedStartDate, filters.selectedEndDate)
        }
    }

    private fun clearFilterViews() {
        binding.layoutConferenceFilters.removeAllViews()
    }

    private fun addFilterViews(filters: List<ConferenceSelectedFilteringOptionUiState>) {
        filters.forEach { binding.layoutConferenceFilters.addView(createFilterTag(it.name)) }
    }

    private fun addDurationFilter(
        startDate: ConferenceSelectedFilteringDateOptionUiState?,
        endDate: ConferenceSelectedFilteringDateOptionUiState?,
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

    private fun initEventFilterButtonClickListener() {
        binding.btnEventFilter.setOnClickListener { navigateToEventFilter() }
    }

    private fun navigateToEventDetail(event: ConferenceItemUiState) {
        EventDetailActivity.startActivity(requireContext(), event.id)
    }

    private fun navigateToEventFilter() {
        val selectedFilter = viewModel.selectedFilter.value
        val filterActivityIntent = ConferenceFilterActivity.createIntent(
            context = requireContext(),
            selectedStatusIds = selectedFilter.selectedStatusFilteringOptionIds,
            selectedTagIds = selectedFilter.selectedTagFilteringOptionIds,
            selectedStartDate = selectedFilter.selectedStartDate?.selectedDate,
            selectedEndDate = selectedFilter.selectedEndDate?.selectedDate,
        )
        filterActivityLauncher.launch(filterActivityIntent)
    }
}
