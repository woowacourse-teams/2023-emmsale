package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingConferenceHistoryBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.ui.onboarding.uistate.ResumeTagUiState
import com.emmsale.presentation.ui.onboarding.uistate.ResumeTagsUiState
import com.emmsale.presentation.utils.views.chipOf
import com.google.android.material.snackbar.Snackbar

class OnboardingConferenceResumeFragment :
    BaseFragment<FragmentOnboardingConferenceHistoryBinding>() {
    override val viewModel: OnboardingViewModel by activityViewModels { OnboardingViewModelFactory() }
    override val layoutResId: Int = R.layout.fragment_onboarding_conference_history

    private lateinit var conferenceResumeAdapter: ResumeTagSpinnerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        setupConferenceSpinner()
        setupConferences()
        setupSelectedConferences()
    }

    private fun setupConferenceSpinner() {
        conferenceResumeAdapter = ResumeTagSpinnerAdapter(mutableListOf())
        binding.spinnerConferenceHistory.adapter = conferenceResumeAdapter
        binding.spinnerConferenceHistory.onItemSelectedListener =
            ConferenceSpinnerSelectedListener()
    }

    private fun setupConferences() {
        viewModel.conferenceTags.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResumeTagsUiState.Success -> updateConferenceSpinner(state.tags)
                is ResumeTagsUiState.Error -> showLoginFailedMessage()
            }
        }
    }

    private fun setupSelectedConferences() {
        viewModel.selectedConferenceTags.observe(viewLifecycleOwner) { conferences ->
            binding.chipgroupConferenceTags.removeAllViews()
            conferences.forEach(::addConferenceChip)
        }
    }

    private fun addConferenceChip(conferenceTag: ResumeTagUiState) {
        binding.chipgroupConferenceTags.addView(
            chipOf(requireContext()) {
                text = conferenceTag.name
                setOnCloseIconClickListener { viewModel.removeConferenceTag(conferenceTag) }
            }
        )
    }

    private fun updateConferenceSpinner(conferenceTags: List<ResumeTagUiState>) {
        conferenceResumeAdapter.updateItems(conferenceTags)
    }

    private fun showLoginFailedMessage() {
        Snackbar.make(binding.root, "데이터를 불러오기에 실패했어요 \uD83D\uDE25", Snackbar.LENGTH_SHORT)
            .show()
    }

    inner class ConferenceSpinnerSelectedListener : OnItemSelectedListener {

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            if (position == 0) return
            viewModel.addConferenceTag(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}
