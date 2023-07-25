package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingEducationCareerBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.common.views.chipOf
import com.emmsale.presentation.ui.onboarding.uistate.CareerContentUiState

class OnboardingEducationCareerFragment :
    BaseFragment<FragmentOnboardingEducationCareerBinding>() {
    val viewModel: OnboardingViewModel by activityViewModels { OnboardingViewModel.factory }
    override val layoutResId: Int = R.layout.fragment_onboarding_education_career

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        setupEducations()
    }

    private fun setupEducations() {
        viewModel.educations.observe(viewLifecycleOwner) { educations ->
            educations?.careerContents?.forEach(::addEducationChip)
        }
    }

    private fun addEducationChip(educationTag: CareerContentUiState) {
        binding.chipgroupEduTags.addView(createChip(educationTag))
    }

    private fun createChip(educationTag: CareerContentUiState) = chipOf {
        text = educationTag.name
        isChecked = educationTag.isSelected
        setOnCheckedChangeListener { _, _ -> viewModel.toggleTagSelection(educationTag) }
    }
}
