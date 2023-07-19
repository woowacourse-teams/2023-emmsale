package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingEducationHistoryBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.ui.onboarding.model.EducationUiState
import com.emmsale.presentation.ui.onboarding.uistate.EducationsUiState
import com.emmsale.presentation.ui.onboarding.uistate.educationChipOf
import com.google.android.material.snackbar.Snackbar

class OnboardingEducationHistoryFragment :
    BaseFragment<FragmentOnboardingEducationHistoryBinding>() {
    override val viewModel: OnboardingViewModel by activityViewModels { OnboardingViewModelFactory() }
    override val layoutResId: Int = R.layout.fragment_onboarding_education_history

    private lateinit var eduHistoryAdapter: EducationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        setupEducationSpinner()
        setupEducations()
        setupSelectedEducations()
    }

    private fun setupEducationSpinner() {
        eduHistoryAdapter = EducationAdapter(mutableListOf())
        binding.spinnerEduHistory.adapter = eduHistoryAdapter
        binding.spinnerEduHistory.onItemSelectedListener = EducationSpinnerSelectedListener()
    }

    private fun setupEducations() {
        viewModel.educations.observe(viewLifecycleOwner) { state ->
            when (state) {
                is EducationsUiState.Success -> updateEducationSpinner(state.educations)
                is EducationsUiState.Error -> showLoginFailedMessage()
            }
        }
    }

    private fun setupSelectedEducations() {
        viewModel.selectedEducations.observe(viewLifecycleOwner) { educations ->
            binding.chipgroupEduHistory.removeAllViews()
            educations.forEach(::addEducationChip)
        }
    }

    private fun addEducationChip(education: EducationUiState) {
        binding.chipgroupEduHistory.addView(
            educationChipOf(requireContext()) {
                text = education.name
                setOnCloseIconClickListener { viewModel.removeEduHistory(education) }
            }
        )
    }

    private fun updateEducationSpinner(educations: List<EducationUiState>) {
        eduHistoryAdapter.updateItems(educations)
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
            viewModel.addEduHistory(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}
