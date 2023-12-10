package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingFieldBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.views.activityChipOf
import com.emmsale.presentation.ui.onboarding.uiState.ActivityUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFieldFragment :
    BaseFragment<FragmentOnboardingFieldBinding>(R.layout.fragment_onboarding_field),
    View.OnClickListener {

    val viewModel: OnboardingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initClickListener()
        setupFields()
    }

    private fun initClickListener() {
        binding.btnNext.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
    }

    private fun setupFields() {
        viewModel.activities.observe(viewLifecycleOwner) { activities ->
            binding.chipgroupFieldTags.removeAllViews()
            activities.fields.forEach(::addFieldChip)
        }
    }

    private fun addFieldChip(fieldTag: ActivityUiState) {
        binding.chipgroupFieldTags.addView(createChip(fieldTag))
    }

    private fun createChip(activity: ActivityUiState) = activityChipOf {
        text = activity.name
        isChecked = activity.isSelected
        setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && viewModel.isExceedFieldLimit) {
                showToast(R.string.onboardingfield_selection_limit_exceed)
                setChecked(false)
                return@setOnCheckedChangeListener
            }

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
