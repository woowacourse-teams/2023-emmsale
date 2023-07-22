package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingJobCareerBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.common.viewModelFactory
import com.emmsale.presentation.common.views.chipOf
import com.emmsale.presentation.ui.onboarding.uistate.CareerContentUiState

class OnboardingJobCareerFragment :
    BaseFragment<FragmentOnboardingJobCareerBinding>() {
    override val viewModel: OnboardingViewModel by activityViewModels { viewModelFactory }
    override val layoutResId: Int = R.layout.fragment_onboarding_job_career

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        setupJobs()
    }

    private fun setupJobs() {
        viewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            jobs?.careerContents?.forEach(::addJobChip)
        }
    }

    private fun addJobChip(jobTag: CareerContentUiState) {
        binding.chipgroupJobTags.addView(createChip(jobTag))
    }

    private fun createChip(jobTag: CareerContentUiState) = chipOf {
        text = jobTag.name
        isChecked = jobTag.isSelected
        setOnCheckedChangeListener { _, _ -> viewModel.toggleJobTag(jobTag) }
    }
}
