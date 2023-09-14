package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingEducationBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.views.activityChipOf
import com.emmsale.presentation.ui.onboarding.uiState.ActivityUiState

class OnboardingEducationFragment :
    BaseFragment<FragmentOnboardingEducationBinding>(),
    View.OnClickListener {
    val viewModel: OnboardingViewModel by activityViewModels { OnboardingViewModel.factory }
    override val layoutResId: Int = R.layout.fragment_onboarding_education

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initClickListener()
        setupEducations()
    }

    private fun initClickListener() {
        binding.btnNext.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
    }

    private fun setupEducations() {
        viewModel.activities.observe(viewLifecycleOwner) { activities ->
            binding.chipgroupEduTags.removeAllViews()
            activities.educations.forEach(::addEducationChip)
        }
    }

    private fun addEducationChip(educationTag: ActivityUiState) {
        binding.chipgroupEduTags.addView(createChip(educationTag))
    }

    private fun createChip(activity: ActivityUiState) = activityChipOf {
        text = activity.name
        isChecked = activity.isSelected
        setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateSelection(activity.id, isChecked)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_next -> (requireActivity() as OnboardingActivity).navigateToNextPage()
            R.id.btn_back -> (requireActivity() as OnboardingActivity).navigateToPrevPage()
        }
    }
}
