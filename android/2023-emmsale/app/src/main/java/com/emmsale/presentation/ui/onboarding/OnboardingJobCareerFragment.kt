package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingJobCareerBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.common.viewModelFactory
import com.emmsale.presentation.common.views.chipOf
import com.emmsale.presentation.ui.onboarding.uistate.CareerContentUiState
import com.google.android.material.snackbar.Snackbar

class OnboardingJobCareerFragment :
    BaseFragment<FragmentOnboardingJobCareerBinding>() {
    override val viewModel: OnboardingViewModel by activityViewModels { viewModelFactory }
    override val layoutResId: Int = R.layout.fragment_onboarding_job_career

    private lateinit var jobCareerAdapter: CareerSpinnerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        setupJobSpinner()
        setupJobs()
        setupSelectedJobs()
    }

    private fun setupJobSpinner() {
        jobCareerAdapter = CareerSpinnerAdapter(mutableListOf())
        binding.spinnerJobCareers.adapter = jobCareerAdapter
        binding.spinnerJobCareers.onItemSelectedListener = JobSpinnerSelectedListener()
    }

    private fun setupJobs() {
        viewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            jobs?.run { updateJobSpinner(this.careerContentsWithCategory) }
        }
    }

    private fun setupSelectedJobs() {
        viewModel.selectedJobTags.observe(viewLifecycleOwner) { jobs ->
            binding.chipgroupJobTags.removeAllViews()
            jobs.forEach(::addJobChip)
        }
    }

    private fun addJobChip(jobTag: CareerContentUiState) {
        binding.chipgroupJobTags.addView(
            chipOf(requireContext()) {
                text = jobTag.name
                setOnCloseIconClickListener { viewModel.removeJobTag(jobTag) }
            }
        )
    }

    private fun updateJobSpinner(jobTags: List<CareerContentUiState>) {
        jobCareerAdapter.updateItems(jobTags)
    }

    private fun showLoginFailedMessage() {
        Snackbar.make(binding.root, "데이터를 불러오기에 실패했어요 \uD83D\uDE25", Snackbar.LENGTH_SHORT)
            .show()
    }

    inner class JobSpinnerSelectedListener : OnItemSelectedListener {

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            if (position == 0) return
            viewModel.addJobTag(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}
