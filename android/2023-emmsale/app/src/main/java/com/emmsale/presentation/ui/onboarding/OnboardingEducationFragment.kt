package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingEducationBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.views.activityChipOf
import com.emmsale.presentation.ui.onboarding.uiState.ActivityUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingEducationFragment :
    BaseFragment<FragmentOnboardingEducationBinding>(R.layout.fragment_onboarding_education) {

    val viewModel: OnboardingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDateBinding()
        setupToolbar()

        observeEducations()
    }

    private fun setupDateBinding() {
        binding.viewModel = viewModel
        binding.onNextButtonClick =
            { (requireActivity() as OnboardingActivity).navigateToNextPage() }
    }

    private fun setupToolbar() {
        binding.tbEducationFragment.setNavigationOnClickListener {
            (requireActivity() as OnboardingActivity).onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeEducations() {
        viewModel.educations.observe(viewLifecycleOwner) { educations ->
            binding.chipgroupEduTags.removeAllViews()
            educations.forEach(::addEducationChip)
        }
    }

    private fun addEducationChip(educationTag: ActivityUiState) {
        binding.chipgroupEduTags.addView(createChip(educationTag))
    }

    private fun createChip(activity: ActivityUiState) = activityChipOf {
        text = activity.activity.name
        isChecked = activity.isSelected
        setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateSelection(activity.activity.id, isChecked)
        }
    }
}
