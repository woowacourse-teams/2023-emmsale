package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingConferenceCareerBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.common.viewModelFactory
import com.emmsale.presentation.common.views.chipOf
import com.emmsale.presentation.ui.onboarding.uistate.CareerContentUiState
import com.google.android.material.snackbar.Snackbar

class OnboardingConferenceCareerFragment :
    BaseFragment<FragmentOnboardingConferenceCareerBinding>() {
    override val viewModel: OnboardingViewModel by activityViewModels { viewModelFactory }
    override val layoutResId: Int = R.layout.fragment_onboarding_conference_career

    private lateinit var conferenceResumeAdapter: CareerTagSpinnerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        setupConferenceSpinner()
        setupConferences()
        setupSelectedConferences()
    }

    private fun setupConferenceSpinner() {
        conferenceResumeAdapter = CareerTagSpinnerAdapter(mutableListOf())
        binding.spinnerConferenceHistory.adapter = conferenceResumeAdapter
        binding.spinnerConferenceHistory.onItemSelectedListener =
            ConferenceSpinnerSelectedListener()
    }

    private fun setupConferences() {
        viewModel.conferences.observe(viewLifecycleOwner) { conference ->
            conference?.run { updateConferenceSpinner(this.careerContentsWithCategory) }
        }
    }

    private fun setupSelectedConferences() {
        viewModel.selectedConferenceTags.observe(viewLifecycleOwner) { conferences ->
            binding.chipgroupConferenceTags.removeAllViews()
            conferences.forEach(::addConferenceChip)
        }
    }

    private fun addConferenceChip(conferenceTag: CareerContentUiState) {
        binding.chipgroupConferenceTags.addView(
            chipOf(requireContext()) {
                text = conferenceTag.name
                setOnCloseIconClickListener { viewModel.removeConferenceTag(conferenceTag) }
            }
        )
    }

    private fun updateConferenceSpinner(conferenceTags: List<CareerContentUiState>) {
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
