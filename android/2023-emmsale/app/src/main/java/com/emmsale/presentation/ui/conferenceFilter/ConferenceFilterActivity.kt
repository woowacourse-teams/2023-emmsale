package com.emmsale.presentation.ui.conferenceFilter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityConferenceFilterBinding
import com.emmsale.databinding.LayoutFilterConferenceDurationBinding
import com.emmsale.databinding.LayoutFilterConferenceStatusBinding
import com.emmsale.databinding.LayoutFilterConferenceTagBinding
import com.emmsale.presentation.common.extension.checkAll
import com.emmsale.presentation.common.extension.getSerializableExtraCompat
import com.emmsale.presentation.common.extension.showDatePickerDialog
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.extension.uncheckAll
import com.emmsale.presentation.common.views.ConfirmDialog
import com.emmsale.presentation.common.views.activityChipOf
import com.emmsale.presentation.ui.conferenceFilter.uiState.ConferenceFilterUiState
import com.emmsale.presentation.ui.conferenceFilter.uiState.ConferenceFilteringDateOptionUiState
import com.emmsale.presentation.ui.conferenceFilter.uiState.ConferenceFilteringOptionUiState
import com.google.android.material.chip.ChipGroup
import java.time.LocalDate

class ConferenceFilterActivity : AppCompatActivity() {
    private val viewModel: ConferenceFilterViewModel by viewModels { ConferenceFilterViewModel.factory }
    private val binding: ActivityConferenceFilterBinding by lazy {
        ActivityConferenceFilterBinding.inflate(layoutInflater)
    }
    private val statusFilterBinding: LayoutFilterConferenceStatusBinding by lazy { binding.layoutFilterStatus }
    private val tagFilterBinding: LayoutFilterConferenceTagBinding by lazy { binding.layoutFilterTag }
    private val durationFilterBinding: LayoutFilterConferenceDurationBinding by lazy { binding.layoutFilterDuration }

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
        ConfirmDialog(
            context = this,
            title = getString(R.string.conferencefilter_clear_title),
            message = getString(R.string.conferencefilter_clear_message),
            onPositiveButtonClick = { viewModel.clearFilters() },
        ).show()
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
        tagFilterBinding.tagAllFilter.setOnClickListener {
            if (tagFilterBinding.tagAllFilter.isChecked) {
                tagFilterBinding.cgConferenceTagChips.checkAll()
            } else {
                tagFilterBinding.cgConferenceTagChips.uncheckAll()
            }
        }
    }

    private fun initDurationButtonClickListener() {
        durationFilterBinding.btnFilterStartDuration.setOnClickListener {
            showDatePickerDialog(viewModel::updateStartDate)
        }

        durationFilterBinding.btnFilterEndDuration.setOnClickListener {
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
        updateStatusFilters(conferenceFilters.statusFilteringOptions)
        updateTagFilters(conferenceFilters.tagFilteringOptions)
        updateConferenceDurations(
            conferenceFilters.selectedStartDate,
            conferenceFilters.selectedEndDate,
        )
    }

    private fun updateStatusFilters(eventStatuses: List<ConferenceFilteringOptionUiState>) {
        removeFilterStatuses()
        eventStatuses.forEach { filter ->
            addTagFilter(statusFilterBinding.cgConferenceStatusChips, filter) {
                viewModel.toggleFilterSelection(filter)
            }
        }
    }

    private fun updateTagFilters(eventTags: List<ConferenceFilteringOptionUiState>) {
        removeTagFiltersExcludingAllTag()
        eventTags.forEach { filter ->
            addTagFilter(tagFilterBinding.cgConferenceTagChips, filter) {
                viewModel.toggleFilterSelection(filter)
            }
        }
    }

    private fun removeFilterStatuses() {
        statusFilterBinding.cgConferenceStatusChips.removeAllViews()
    }

    private fun removeTagFiltersExcludingAllTag() {
        val startTagWithoutAllTagPosition = 1

        tagFilterBinding.cgConferenceTagChips.removeViews(
            startTagWithoutAllTagPosition,
            tagFilterBinding.cgConferenceTagChips.childCount - 1,
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
        durationFilterBinding.btnFilterStartDuration.text = startDate?.transformToDateString(this)
        durationFilterBinding.btnFilterEndDuration.text = endDate?.transformToDateString(this)

        durationFilterBinding.btnFilterStartDuration.isActive = (startDate != null)
        durationFilterBinding.btnFilterEndDuration.isActive = (endDate != null)
    }

    private fun setupIsEventTagAllSelected() {
        viewModel.isTagAllSelected.observe(this) { isTagAllSelected ->
            updateTagFilterViews(isTagAllSelected)
        }
    }

    private fun updateTagFilterViews(isTagAllSelected: Boolean) {
        tagFilterBinding.tagAllFilter.isChecked = isTagAllSelected
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
            durationFilterBinding.btnFilterEndDuration.isEnabled = isSelected
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
            ConfirmDialog(
                context = this@ConferenceFilterActivity,
                title = getString(R.string.conferencefilter_backpress_dialog_title),
                message = getString(R.string.conferencefilter_backpress_dialog_message),
                onPositiveButtonClick = { finish() },
            ).show()
        }
    }
}
