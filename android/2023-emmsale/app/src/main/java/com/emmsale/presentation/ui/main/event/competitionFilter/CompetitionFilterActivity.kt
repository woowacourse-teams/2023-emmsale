package com.emmsale.presentation.ui.main.event.competitionFilter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityCompetitionFilterBinding
import com.emmsale.databinding.LayoutFilterCompetitionDurationBinding
import com.emmsale.databinding.LayoutFilterCompetitionStatusBinding
import com.emmsale.databinding.LayoutFilterCompetitionTagBinding
import com.emmsale.presentation.common.extension.checkAll
import com.emmsale.presentation.common.extension.getSerializableExtraCompat
import com.emmsale.presentation.common.extension.showDatePickerDialog
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.extension.uncheckAll
import com.emmsale.presentation.common.views.ConfirmDialog
import com.emmsale.presentation.common.views.activityChipOf
import com.emmsale.presentation.ui.main.event.competitionFilter.uistate.CompetitionFilterUiState
import com.emmsale.presentation.ui.main.event.competitionFilter.uistate.CompetitionFilteringDateOptionUiState
import com.emmsale.presentation.ui.main.event.competitionFilter.uistate.CompetitionFilteringOptionUiState
import com.google.android.material.chip.ChipGroup
import java.time.LocalDate

class CompetitionFilterActivity : AppCompatActivity() {
    private val viewModel: CompetitionFilterViewModel by viewModels { CompetitionFilterViewModel.factory }
    private val binding: ActivityCompetitionFilterBinding by lazy {
        ActivityCompetitionFilterBinding.inflate(layoutInflater)
    }
    private val statusFilterBinding: LayoutFilterCompetitionStatusBinding by lazy { binding.layoutFilterStatus }
    private val tagFilterBinding: LayoutFilterCompetitionTagBinding by lazy { binding.layoutFilterTag }
    private val durationFilterBinding: LayoutFilterCompetitionDurationBinding by lazy { binding.layoutFilterDuration }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        initBackPressedDispatcher()
        setupIsEventTagAllSelected()
        setupIsStartDateSelected()
        setupCompetitionFilter()
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
            title = getString(R.string.competitionfilter_clear_title),
            message = getString(R.string.competitionfilter_clear_message),
            onPositiveButtonClick = { viewModel.clearFilters() },
        ).show()
    }

    private fun initEventFilterApplyButtonClickListener() {
        binding.btnFilterApply.setOnClickListener { finishWithSendingSelectedFilterIds() }
    }

    private fun finishWithSendingSelectedFilterIds() {
        val competitionFilter = viewModel.competitionFilter.value
        val filterIdsIntent = Intent()
            .putExtra(
                KEY_SELECTED_COMPETITION_STATUS_FILTER_IDS,
                competitionFilter.selectedStatusFilteringOptionIds.toLongArray(),
            )
            .putExtra(
                KEY_SELECTED_COMPETITION_TAG_FILTER_IDS,
                competitionFilter.selectedTagFilteringOptionIds.toLongArray(),
            )
            .putExtra(KEY_SELECTED_START_DATE, competitionFilter.selectedStartDate?.date)
            .putExtra(KEY_SELECTED_END_DATE, competitionFilter.selectedEndDate?.date)

        setResult(RESULT_OK, filterIdsIntent)
        finish()
    }

    private fun initTagAllFilterButtonClickListener() {
        tagFilterBinding.tagAllFilter.setOnClickListener {
            if (tagFilterBinding.tagAllFilter.isChecked) {
                tagFilterBinding.cgCompetitionTagChips.checkAll()
            } else {
                tagFilterBinding.cgCompetitionTagChips.uncheckAll()
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
        onBackPressedDispatcher.addCallback(this, CompetitionFilterOnBackPressedCallback())
    }

    private fun setupCompetitionFilter() {
        viewModel.competitionFilter.observe(this) { eventFilters ->
            when {
                eventFilters.isLoadingCompetitionFilterFailed -> showLoadingCompetitionFilterFailed()
                !eventFilters.isLoading -> updateFilterViews(eventFilters)
            }
        }
    }

    private fun showLoadingCompetitionFilterFailed() {
        showToast(getString(R.string.all_data_loading_failed_message))
    }

    private fun updateFilterViews(competitionFilters: CompetitionFilterUiState) {
        updateStatusFilters(competitionFilters.statusFilteringOptions)
        updateTagFilters(competitionFilters.tagFilteringOptions)
        updateDurationsFilters(
            competitionFilters.selectedStartDate,
            competitionFilters.selectedEndDate,
        )
    }

    private fun updateStatusFilters(eventStatuses: List<CompetitionFilteringOptionUiState>) {
        removeFilterStatuses()
        eventStatuses.forEach { filter ->
            addTagFilter(statusFilterBinding.cgCompetitionStatusChips, filter) {
                viewModel.toggleFilterSelection(filter)
            }
        }
    }

    private fun updateTagFilters(eventTags: List<CompetitionFilteringOptionUiState>) {
        removeTagFiltersExcludingAllTag()
        eventTags.forEach { filter ->
            addTagFilter(tagFilterBinding.cgCompetitionTagChips, filter) {
                viewModel.toggleFilterSelection(filter)
            }
        }
    }

    private fun removeFilterStatuses() {
        statusFilterBinding.cgCompetitionStatusChips.removeAllViews()
    }

    private fun removeTagFiltersExcludingAllTag() {
        val startTagWithoutAllTagPosition = 1

        tagFilterBinding.cgCompetitionTagChips.removeViews(
            startTagWithoutAllTagPosition,
            tagFilterBinding.cgCompetitionTagChips.childCount - 1,
        )
    }

    private fun addTagFilter(
        chipGroup: ChipGroup,
        tag: CompetitionFilteringOptionUiState,
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

    private fun updateDurationsFilters(
        startDate: CompetitionFilteringDateOptionUiState?,
        endDate: CompetitionFilteringDateOptionUiState?,
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
        val selectedCompetitionStatusFilterIds = intent
            ?.getLongArrayExtra(KEY_SELECTED_COMPETITION_STATUS_FILTER_IDS)
            ?.toTypedArray() ?: emptyArray()
        val selectedCompetitionTagFilterIds = intent
            ?.getLongArrayExtra(KEY_SELECTED_COMPETITION_TAG_FILTER_IDS)
            ?.toTypedArray() ?: emptyArray()
        val startDate = intent.getSerializableExtraCompat<LocalDate>(KEY_SELECTED_START_DATE)
        val endDate = intent.getSerializableExtraCompat<LocalDate>(KEY_SELECTED_END_DATE)

        viewModel.updateFilteringOptionsToSelectedState(
            selectedCompetitionStatusFilterIds,
            selectedCompetitionTagFilterIds,
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
        const val KEY_SELECTED_COMPETITION_STATUS_FILTER_IDS = "selected_competition_status_ids_key"
        const val KEY_SELECTED_COMPETITION_TAG_FILTER_IDS = "selected_competition_tag_ids_key"
        const val KEY_SELECTED_START_DATE = "selected_start_date_key"
        const val KEY_SELECTED_END_DATE = "selected_end_date_key"

        fun getIntent(
            context: Context,
            selectedStatusIds: Array<Long> = emptyArray(),
            selectedTagIds: Array<Long> = emptyArray(),
            selectedStartDate: LocalDate? = null,
            selectedEndDate: LocalDate? = null,
        ): Intent = Intent(context, CompetitionFilterActivity::class.java)
            .putExtra(KEY_SELECTED_COMPETITION_STATUS_FILTER_IDS, selectedStatusIds.toLongArray())
            .putExtra(KEY_SELECTED_COMPETITION_TAG_FILTER_IDS, selectedTagIds.toLongArray())
            .putExtra(KEY_SELECTED_START_DATE, selectedStartDate)
            .putExtra(KEY_SELECTED_END_DATE, selectedEndDate)
    }

    inner class CompetitionFilterOnBackPressedCallback : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showBackPressDialog()
        }

        private fun showBackPressDialog() {
            ConfirmDialog(
                context = this@CompetitionFilterActivity,
                title = getString(R.string.competitionfilter_backpress_dialog_title),
                message = getString(R.string.competitionfilter_backpress_dialog_message),
                onPositiveButtonClick = { finish() },
            ).show()
        }
    }
}
