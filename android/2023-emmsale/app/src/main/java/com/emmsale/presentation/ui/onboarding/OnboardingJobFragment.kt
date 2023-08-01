package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingJobBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.common.views.activityChipOf
import com.emmsale.presentation.ui.onboarding.uistate.ActivityUiState

class OnboardingJobFragment : BaseFragment<FragmentOnboardingJobBinding>() {
    val viewModel: OnboardingViewModel by activityViewModels { OnboardingViewModel.factory }
    override val layoutResId: Int = R.layout.fragment_onboarding_job

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initClickListener()
        setupJobs()
    }

    private fun initClickListener() {
        binding.btnNext.setOnClickListener {
            (requireActivity() as OnboardingActivity).navigateToNextPage()
        }
    }

    private fun setupJobs() {
        viewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            jobs?.activities?.forEach(::addJobChip)
        }
    }

    private fun addJobChip(jobTag: ActivityUiState) {
        binding.chipgroupJobTags.addView(createChip(jobTag))
    }

    private fun createChip(jobTag: ActivityUiState) = activityChipOf {
        text = jobTag.name
        isChecked = jobTag.isSelected
        setOnCheckedChangeListener { _, _ -> viewModel.toggleTagSelection(jobTag) }
    }
}
