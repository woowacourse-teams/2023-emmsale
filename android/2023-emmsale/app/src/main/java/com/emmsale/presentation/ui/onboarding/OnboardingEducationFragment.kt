package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingEducationBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.common.views.activityChipOf
import com.emmsale.presentation.ui.onboarding.uistate.ActivityUiState

class OnboardingEducationFragment : BaseFragment<FragmentOnboardingEducationBinding>(),
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
    }

    private fun setupEducations() {
        viewModel.educations.observe(viewLifecycleOwner) { educations ->
            educations?.activities?.forEach(::addEducationChip)
        }
    }

    private fun addEducationChip(educationTag: ActivityUiState) {
        binding.chipgroupEduTags.addView(createChip(educationTag))
    }

    private fun createChip(educationTag: ActivityUiState) = activityChipOf {
        text = educationTag.name
        isChecked = educationTag.isSelected
        setOnCheckedChangeListener { _, _ -> viewModel.toggleTagSelection(educationTag) }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_next -> (requireActivity() as OnboardingActivity).navigateToNextPage()
        }
    }
}
