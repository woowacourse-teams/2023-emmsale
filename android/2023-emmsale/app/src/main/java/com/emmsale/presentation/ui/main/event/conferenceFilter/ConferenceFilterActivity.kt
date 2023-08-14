package com.emmsale.presentation.ui.main.event.conferenceFilter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityConferenceFilterBinding
import com.emmsale.databinding.LayoutFilterConferenceDurationBinding
import com.emmsale.databinding.LayoutFilterConferenceEventTagBinding
import com.emmsale.databinding.LayoutFilterConferenceStatusBinding
import com.emmsale.presentation.common.extension.checkAll
import com.emmsale.presentation.common.extension.getSerializableExtraCompat
import com.emmsale.presentation.common.extension.message
import com.emmsale.presentation.common.extension.negativeButton
import com.emmsale.presentation.common.extension.positiveButton
import com.emmsale.presentation.common.extension.showDatePickerDialog
import com.emmsale.presentation.common.extension.showDialog
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.extension.title
import com.emmsale.presentation.common.extension.uncheckAll
import com.emmsale.presentation.common.views.activityChipOf
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilterUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilteringDateOptionUiState
import com.emmsale.presentation.ui.main.event.conferenceFilter.uistate.ConferenceFilteringOptionUiState
import com.google.android.material.chip.ChipGroup
import java.time.LocalDate

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
        setupIsEventTagAllSelected()
        setupIsStartDateSelected()
        setupConferenceFilter()
        fetchFilters()
    }

    private fun initView() {
        initEventFilterToolbarClickListener()
        initAllFilteringOptionsClearButtonClickListener()
        initEventFilterApplyButtonClickListener()
        initTagAllFilterButtonClickListener()
        initDurationButtonClickListener()
    }

    private fun initEventFilterToolbarClickListener() {
        binding.tbEventFilter.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun initAllFilteringOptionsClearButtonClickListener() {
        binding.btnAllFilteringOptionsClear.setOnClickListener { askFilterClear() }
    }

    private fun askFilterClear() {
        showDialog {
            title(getString(R.string.conferencefilter_clear_title))
            message(getString(R.string.conferencefilter_clear_message))
            positiveButton(getString(R.string.all_okay)) { viewModel.clearFilters() }
            negativeButton(getString(R.string.all_cancel))
        }
    }

    private fun initEventFilterApplyButtonClickListener() {
        binding.btnFilterApply.setOnClickListener { finishWithSendingSelectedFilterIds() }
    }

    private fun finishWithSendingSelectedFilterIds() {
        val conferenceFilter = viewModel.conferenceFilter.value
        val filterIdsIntent = Intent()
            .putExtra(
                KEY_SELECTED_CONFERENCE_STATUS_FILTER_IDS,
                conferenceFilter.selectedStatusFilteringOptionIds.toLongArray(),
            )
            .putExtra(
                KEY_SELECTED_CONFERENCE_TAG_FILTER_IDS,
                conferenceFilter.selectedTagFilteringOptionIds.toLongArray(),
            )
            .putExtra(KEY_SELECTED_START_DATE, conferenceFilter.selectedStartDate?.date)
            .putExtra(KEY_SELECTED_END_DATE, conferenceFilter.selectedEndDate?.date)

        setResult(RESULT_OK, filterIdsIntent)
        finish()
    }

    private fun initTagAllFilterButtonClickListener() {
        eventTagBinding.tagAllFilter.setOnClickListener {
            if (eventTagBinding.tagAllFilter.isChecked) {
                eventTagBinding.cgConferenceTagChips.checkAll()
            } else {
                eventTagBinding.cgConferenceTagChips.uncheckAll()
            }
        }
    }

    private fun initDurationButtonClickListener() {
        eventDurationBinding.btnFilterStartDuration.setOnClickListener {
            showDatePickerDialog(viewModel::updateStartDate)
        }

        eventDurationBinding.btnFilterEndDuration.setOnClickListener {
            showDatePickerDialog(viewModel::updateEndDate)
        }
    }

    private fun initBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(this, ConferenceFilterOnBackPressedCallback())
    }

    private fun setupConferenceFilter() {
        viewModel.conferenceFilter.observe(this) { eventFilters ->
            when {
                eventFilters.isLoadingConferenceFilterFailed -> showLoadingConferenceFilterFailed()
                !eventFilters.isLoading -> updateFilterViews(eventFilters)
            }
        }
    }

    private fun showLoadingConferenceFilterFailed() {
        showToast(getString(R.string.all_data_loading_failed_message))
    }

    private fun updateFilterViews(conferenceFilters: ConferenceFilterUiState) {
        updateStatusFilters(conferenceFilters.conferenceStatusFilteringOptions)
        updateTagFilters(conferenceFilters.conferenceTagFilteringOptions)
        updateConferenceDurations(
            conferenceFilters.selectedStartDate,
            conferenceFilters.selectedEndDate,
        )
    }

    private fun updateStatusFilters(eventStatuses: List<ConferenceFilteringOptionUiState>) {
        removeFilterStatuses()
        eventStatuses.forEach { filter ->
            addTagFilter(eventStatusBinding.cgConferenceStatusChips, filter) {
                viewModel.toggleFilterSelection(filter)
            }
        }
    }

    private fun updateTagFilters(eventTags: List<ConferenceFilteringOptionUiState>) {
        removeTagFiltersExcludingAllTag()
        eventTags.forEach { filter ->
            addTagFilter(eventTagBinding.cgConferenceTagChips, filter) {
                viewModel.toggleFilterSelection(filter)
            }
        }
    }

    private fun removeFilterStatuses() {
        eventStatusBinding.cgConferenceStatusChips.removeAllViews()
    }

    private fun removeTagFiltersExcludingAllTag() {
        val startTagWithoutAllTagPosition = 1

        eventTagBinding.cgConferenceTagChips.removeViews(
            startTagWithoutAllTagPosition,
            eventTagBinding.cgConferenceTagChips.childCount - 1,
        )
    }

    private fun addTagFilter(
        chipGroup: ChipGroup,
        tag: ConferenceFilteringOptionUiState,
        block: () -> Unit,
    ) {
        chipGroup.addView(
            activityChipOf {
                text = tag.name
                isChecked = tag.isSelected
                setOnCheckedChangeListener { _, _ -> block() }
            },
        )
    }

    private fun updateConferenceDurations(
        startDate: ConferenceFilteringDateOptionUiState?,
        endDate: ConferenceFilteringDateOptionUiState?,
    ) {
        eventDurationBinding.btnFilterStartDuration.text = startDate?.transformToDateString(this)
        eventDurationBinding.btnFilterEndDuration.text = endDate?.transformToDateString(this)

        eventDurationBinding.btnFilterStartDuration.isActive = (startDate != null)
        eventDurationBinding.btnFilterEndDuration.isActive = (endDate != null)
    }

    private fun setupIsEventTagAllSelected() {
        viewModel.isTagAllSelected.observe(this) { isTagAllSelected ->
            updateTagFilterViews(isTagAllSelected)
        }
    }

    private fun updateTagFilterViews(isTagAllSelected: Boolean) {
        eventTagBinding.tagAllFilter.isChecked = isTagAllSelected
    }

    private fun fetchFilters() {
        val selectedConferenceStatusFilterIds = intent
            ?.getLongArrayExtra(KEY_SELECTED_CONFERENCE_STATUS_FILTER_IDS)
            ?.toTypedArray() ?: emptyArray()
        val selectedConferenceTagFilterIds = intent
            ?.getLongArrayExtra(KEY_SELECTED_CONFERENCE_TAG_FILTER_IDS)
            ?.toTypedArray() ?: emptyArray()
        val startDate = intent.getSerializableExtraCompat<LocalDate>(KEY_SELECTED_START_DATE)
        val endDate = intent.getSerializableExtraCompat<LocalDate>(KEY_SELECTED_END_DATE)

        viewModel.updateFilteringOptionsToSelectedState(
            selectedConferenceStatusFilterIds,
            selectedConferenceTagFilterIds,
            startDate,
            endDate,
        )
    }

    private fun setupIsStartDateSelected() {
        viewModel.isStartDateSelected.observe(this) { isSelected ->
            eventDurationBinding.btnFilterEndDuration.isEnabled = isSelected
        }
    }

    companion object {
        const val KEY_SELECTED_CONFERENCE_STATUS_FILTER_IDS = "selected_conference_status_ids_key"
        const val KEY_SELECTED_CONFERENCE_TAG_FILTER_IDS = "selected_conference_tag_ids_key"
        const val KEY_SELECTED_START_DATE = "selected_start_date_key"
        const val KEY_SELECTED_END_DATE = "selected_end_date_key"

        fun getIntent(
            context: Context,
            selectedStatusIds: Array<Long> = emptyArray(),
            selectedTagIds: Array<Long> = emptyArray(),
            selectedStartDate: LocalDate? = null,
            selectedEndDate: LocalDate? = null,
        ): Intent = Intent(context, ConferenceFilterActivity::class.java)
            .putExtra(KEY_SELECTED_CONFERENCE_STATUS_FILTER_IDS, selectedStatusIds.toLongArray())
            .putExtra(KEY_SELECTED_CONFERENCE_TAG_FILTER_IDS, selectedTagIds.toLongArray())
            .putExtra(KEY_SELECTED_START_DATE, selectedStartDate)
            .putExtra(KEY_SELECTED_END_DATE, selectedEndDate)
    }

    inner class ConferenceFilterOnBackPressedCallback : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showBackPressDialog()
        }

        private fun showBackPressDialog() {
            showDialog {
                title(getString(R.string.conferencefilter_backpress_dialog_title))
                message(getString(R.string.conferencefilter_backpress_dialog_message))
                positiveButton { finish() }
                negativeButton { }
            }
        }
    }
}
