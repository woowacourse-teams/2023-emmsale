package com.emmsale.presentation.ui.conferenceList

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.emmsale.R
import com.emmsale.data.model.Event
import com.emmsale.databinding.FragmentConferenceBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.ScrollTopListener
import com.emmsale.presentation.common.extension.getSerializableExtraCompat
import com.emmsale.presentation.common.views.FilterTag
import com.emmsale.presentation.common.views.filterChipOf
import com.emmsale.presentation.ui.conferenceFilter.ConferenceFilterActivity
import com.emmsale.presentation.ui.conferenceList.recyclerView.EventRecyclerViewAdapter
import com.emmsale.presentation.ui.conferenceList.uiState.ConferenceSelectedFilteringDateOptionUiState
import com.emmsale.presentation.ui.conferenceList.uiState.ConferenceSelectedFilteringOptionUiState
import com.emmsale.presentation.ui.eventDetail.EventDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

@AndroidEntryPoint
class ConferenceFragment : BaseFragment<FragmentConferenceBinding>(R.layout.fragment_conference) {

    private val viewModel: ConferenceViewModel by viewModels()
    private val eventAdapter by lazy {
        EventRecyclerViewAdapter(
            fragment = this,
            onClickConference = ::navigateToEventDetail,
            onPreloaderReady = { binding.rvEvents.addOnScrollListener(it) },
        )
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

        if (savedInstanceState != null) {
            val index = savedInstanceState.getInt(STATE_POSITION_INDEX)
            val offset = savedInstanceState.getInt(STATE_POSITION_OFFSET)
            val layoutManager = binding.rvEvents.layoutManager as LinearLayoutManager
            lifecycleScope.launch {
                delay(DELAY_RECYCLER_VIEW_ITEM_READY)
                layoutManager.scrollToPositionWithOffset(index, offset)
            }
        }
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
        binding.rvEvents.addOnScrollListener(
            ScrollTopListener(targetView = binding.fabScrollTop),
        )
    }

    private fun setupEventsObserver() {
        viewModel.conferences.observe(viewLifecycleOwner) { eventsResult ->
            when {
                !eventsResult.isLoading -> eventAdapter.submitList(eventsResult.events) {
                    binding.rvEvents.scrollToPosition(0)
                }
            }
        }
    }

    private fun setupFiltersObserver() {
        viewModel.selectedFilter.observe(viewLifecycleOwner) { filters ->
            clearFilterViews()
            addFilterViews(filters.tagFilteringOptions + filters.statusFilteringOptions)
            addDurationFilter(filters.selectedStartDate, filters.selectedEndDate)
        }
    }

    private fun clearFilterViews() {
        binding.layoutConferenceFilters.removeAllViews()
    }

    private fun addFilterViews(filters: List<ConferenceSelectedFilteringOptionUiState>) {
        filters.forEach {
            binding.layoutConferenceFilters.addView(
                createFilterTag(
                    title = it.name,
                    onClick = { viewModel.removeFilteringOptionBy(it.id) },
                ),
            )
        }
    }

    private fun addDurationFilter(
        startDate: ConferenceSelectedFilteringDateOptionUiState?,
        endDate: ConferenceSelectedFilteringDateOptionUiState?,
    ) {
        val startDateString = startDate?.transformToDateString(requireContext())
        val endDateString = endDate?.transformToDateString(requireContext(), true) ?: ""
        val durationString = "$startDateString$endDateString"

        if (startDateString == null) return

        binding.layoutConferenceFilters.addView(
            createFilterTag(
                title = durationString,
                onClick = { viewModel.removeDurationFilteringOption() },
            ),
        )
    }

    private fun createFilterTag(title: String, onClick: () -> Unit): FilterTag = filterChipOf {
        text = title
        setOnClickListener { onClick() }
    }

    private fun initEventFilterButtonClickListener() {
        binding.btnEventFilter.setOnClickListener { navigateToEventFilter() }
    }

    private fun navigateToEventDetail(event: Event) {
        EventDetailActivity.startActivity(requireContext(), event.id)
    }

    private fun navigateToEventFilter() {
        val selectedFilter = viewModel.selectedFilter.value
        val filterActivityIntent = ConferenceFilterActivity.getIntent(
            context = requireContext(),
            selectedStatusIds = selectedFilter.selectedStatusFilteringOptionIds,
            selectedTagIds = selectedFilter.selectedTagFilteringOptionIds,
            selectedStartDate = selectedFilter.selectedStartDate?.date,
            selectedEndDate = selectedFilter.selectedEndDate?.date,
        )
        filterActivityLauncher.launch(filterActivityIntent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val layoutManager = binding.rvEvents.layoutManager as LinearLayoutManager
        val index = layoutManager.findFirstVisibleItemPosition()
        val topView = binding.rvEvents.getChildAt(0)
        val offset = topView.top
        outState.putInt(STATE_POSITION_INDEX, index)
        outState.putInt(STATE_POSITION_OFFSET, offset)
    }

    companion object {
        private const val STATE_POSITION_INDEX = "STATE_POSITION_INDEX"
        private const val STATE_POSITION_OFFSET = "STATE_POSITION_OFFSET"
        private const val DELAY_RECYCLER_VIEW_ITEM_READY = 300L
    }
}
