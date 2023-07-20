package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingEducationHistoryBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.ui.onboarding.uistate.ResumeTagUiState
import com.emmsale.presentation.ui.onboarding.uistate.ResumeTagsUiState
import com.emmsale.presentation.utils.views.chipOf
import com.google.android.material.snackbar.Snackbar

class OnboardingEducationResumeFragment :
    BaseFragment<FragmentOnboardingEducationHistoryBinding>() {
    override val viewModel: OnboardingViewModel by activityViewModels { OnboardingViewModelFactory() }
    override val layoutResId: Int = R.layout.fragment_onboarding_education_history

    private lateinit var educationResumeAdapter: ResumeTagSpinnerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        setupEducationSpinner()
        setupEducations()
        setupSelectedEducations()
    }

    private fun setupEducationSpinner() {
        educationResumeAdapter = ResumeTagSpinnerAdapter(mutableListOf())
        binding.spinnerEduHistory.adapter = educationResumeAdapter
        binding.spinnerEduHistory.onItemSelectedListener = EducationSpinnerSelectedListener()
    }

    private fun setupEducations() {
        viewModel.educationTags.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ResumeTagsUiState.Success -> updateEducationSpinner(state.tags)
                is ResumeTagsUiState.Error -> showLoginFailedMessage()
            }
        }
    }

    private fun setupSelectedEducations() {
        viewModel.selectedEducationTags.observe(viewLifecycleOwner) { educations ->
            binding.chipgroupEduTags.removeAllViews()
            educations.forEach(::addEducationChip)
        }
    }

    private fun addEducationChip(educationTag: ResumeTagUiState) {
        binding.chipgroupEduTags.addView(
            chipOf(requireContext()) {
                text = educationTag.name
                setOnCloseIconClickListener { viewModel.removeEducationTag(educationTag) }
            }
        )
    }

    private fun updateEducationSpinner(educationTags: List<ResumeTagUiState>) {
        educationResumeAdapter.updateItems(educationTags)
    }

    private fun showLoginFailedMessage() {
        Snackbar.make(binding.root, "데이터를 불러오기에 실패했어요 \uD83D\uDE25", Snackbar.LENGTH_SHORT)
            .show()
    }

    inner class EducationSpinnerSelectedListener : OnItemSelectedListener {

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            if (position == 0) return
            viewModel.addEducationTag(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}
