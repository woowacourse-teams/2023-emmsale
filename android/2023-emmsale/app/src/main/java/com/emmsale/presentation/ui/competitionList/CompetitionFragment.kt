package com.emmsale.presentation.ui.competitionList

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.data.model.Event
import com.emmsale.databinding.FragmentCompetitionBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.ScrollTopListener
import com.emmsale.presentation.common.extension.getSerializableExtraCompat
import com.emmsale.presentation.common.views.FilterTag
import com.emmsale.presentation.common.views.filterChipOf
import com.emmsale.presentation.ui.competitionFilter.CompetitionFilterActivity
import com.emmsale.presentation.ui.competitionList.recyclerView.CompetitionRecyclerViewAdapter
import com.emmsale.presentation.ui.competitionList.uiState.CompetitionSelectedFilteringDateOptionUiState
import com.emmsale.presentation.ui.competitionList.uiState.CompetitionSelectedFilteringOptionUiState
import com.emmsale.presentation.ui.eventDetail.EventDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class CompetitionFragment :
    BaseFragment<FragmentCompetitionBinding>(R.layout.fragment_competition) {

    private val viewModel: CompetitionViewModel by viewModels()
    private val eventAdapter: CompetitionRecyclerViewAdapter by lazy {
        CompetitionRecyclerViewAdapter(::navigateToEventDetail)
    }
    private val filterActivityLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result == null || result.resultCode != RESULT_OK) return@registerForActivityResult

            val selectedStatusFilterIds = result.data
                ?.getLongArrayExtra(CompetitionFilterActivity.KEY_SELECTED_COMPETITION_STATUS_FILTER_IDS)
                ?.toTypedArray() ?: emptyArray()
            val selectedTagFilterIds: Array<Long> = result.data
                ?.getLongArrayExtra(CompetitionFilterActivity.KEY_SELECTED_COMPETITION_TAG_FILTER_IDS)
                ?.toTypedArray() ?: emptyArray()
            val selectedStartDate =
                result.data?.getSerializableExtraCompat<LocalDate>(CompetitionFilterActivity.KEY_SELECTED_START_DATE)
            val selectedEndDate =
                result.data?.getSerializableExtraCompat<LocalDate>(CompetitionFilterActivity.KEY_SELECTED_END_DATE)

            viewModel.fetchFilteredCompetitions(
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
        binding.rvEvents.addOnScrollListener(
            ScrollTopListener(targetView = binding.fabScrollTop),
        )
    }

    private fun setupEventsObserver() {
        viewModel.competitions.observe(viewLifecycleOwner) { eventsResult ->
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
            addFilterViews(filters.competitionTagFilteringOptions + filters.competitionStatusFilteringOptions)
            addDurationFilter(filters.selectedStartDate, filters.selectedEndDate)
        }
    }

    private fun clearFilterViews() {
        binding.layoutCompetitionFilters.removeAllViews()
    }

    private fun addFilterViews(filters: List<CompetitionSelectedFilteringOptionUiState>) {
        filters.forEach {
            binding.layoutCompetitionFilters.addView(
                createFilterTag(
                    title = it.name,
                    onClick = { viewModel.removeFilteringOptionBy(it.id) },
                ),
            )
        }
    }

    private fun addDurationFilter(
        startDate: CompetitionSelectedFilteringDateOptionUiState?,
        endDate: CompetitionSelectedFilteringDateOptionUiState?,
    ) {
        val startDateString = startDate?.transformToDateString(requireContext())
        val endDateString = endDate?.transformToDateString(requireContext(), true) ?: ""
        val durationString = "$startDateString$endDateString"

        if (startDateString == null) return

        binding.layoutCompetitionFilters.addView(
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
        val filterActivityIntent = CompetitionFilterActivity.getIntent(
            context = requireContext(),
            selectedStatusIds = selectedFilter.selectedStatusFilteringOptionIds,
            selectedTagIds = selectedFilter.selectedTagFilteringOptionIds,
            selectedStartDate = selectedFilter.selectedStartDate?.date,
            selectedEndDate = selectedFilter.selectedEndDate?.date,
        )
        filterActivityLauncher.launch(filterActivityIntent)
    }
}
