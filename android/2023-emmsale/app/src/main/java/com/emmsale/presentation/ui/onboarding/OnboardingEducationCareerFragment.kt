package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingEducationCareerBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.common.viewModelFactory
import com.emmsale.presentation.common.views.chipOf
import com.emmsale.presentation.ui.onboarding.uistate.CareerContentUiState
import com.google.android.material.snackbar.Snackbar

class OnboardingEducationCareerFragment :
    BaseFragment<FragmentOnboardingEducationCareerBinding>() {
    override val viewModel: OnboardingViewModel by activityViewModels { viewModelFactory }
    override val layoutResId: Int = R.layout.fragment_onboarding_education_career

    private lateinit var educationCareerAdapter: CareerSpinnerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        setupEducationSpinner()
        setupEducations()
        setupSelectedEducations()
    }

    private fun setupEducationSpinner() {
        educationCareerAdapter = CareerSpinnerAdapter(mutableListOf())
        binding.spinnerEduCareers.adapter = educationCareerAdapter
        binding.spinnerEduCareers.onItemSelectedListener = EducationSpinnerSelectedListener()
    }

    private fun setupEducations() {
        viewModel.educations.observe(viewLifecycleOwner) { educations ->
            educations?.run { updateEducationSpinner(this.careerContentsWithCategory) }
        }
    }

    private fun setupSelectedEducations() {
        viewModel.selectedEducationTags.observe(viewLifecycleOwner) { educations ->
            binding.chipgroupEduTags.removeAllViews()
            educations.forEach(::addEducationChip)
        }
    }

    private fun addEducationChip(educationTag: CareerContentUiState) {
        binding.chipgroupEduTags.addView(
            chipOf(requireContext()) {
                text = educationTag.name
                setOnCloseIconClickListener { viewModel.removeEducationTag(educationTag) }
            }
        )
    }

    private fun updateEducationSpinner(educationTags: List<CareerContentUiState>) {
        educationCareerAdapter.updateItems(educationTags)
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
