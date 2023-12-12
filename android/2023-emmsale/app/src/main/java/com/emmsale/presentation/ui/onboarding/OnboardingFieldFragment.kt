package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingFieldBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.views.activityChipOf
import com.emmsale.presentation.ui.onboarding.uiState.ActivityUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFieldFragment :
    BaseFragment<FragmentOnboardingFieldBinding>(R.layout.fragment_onboarding_field) {

    val viewModel: OnboardingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDataBinding()
        setupToolbar()

        observeFields()
    }

    private fun setupDataBinding() {
        binding.viewModel = viewModel
        binding.onNextButtonClick =
            { (requireActivity() as OnboardingActivity).navigateToNextPage() }
    }

    private fun setupToolbar() {
        binding.tbFieldFragment.setNavigationOnClickListener {
            (requireActivity() as OnboardingActivity).onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeFields() {
        viewModel.fields.observe(viewLifecycleOwner) { fields ->
            binding.chipgroupFieldTags.removeAllViews()
            fields.forEach(::addFieldChip)
        }
    }

    private fun addFieldChip(fieldTag: ActivityUiState) {
        binding.chipgroupFieldTags.addView(createChip(fieldTag))
    }

    private fun createChip(activity: ActivityUiState) = activityChipOf {
        text = activity.activity.name
        isChecked = activity.isSelected
        setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateSelection(activity.activity.id, isChecked)
        }
    }
}
