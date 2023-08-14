package com.emmsale.presentation.ui.onboarding.field

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingFieldBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.views.activityChipOf
import com.emmsale.presentation.ui.onboarding.OnboardingActivity
import com.emmsale.presentation.ui.onboarding.OnboardingViewModel
import com.emmsale.presentation.ui.onboarding.uistate.ActivityUiState

class OnboardingFieldFragment :
    BaseFragment<FragmentOnboardingFieldBinding>(),
    View.OnClickListener {
    val viewModel: OnboardingViewModel by activityViewModels { OnboardingViewModel.factory }
    override val layoutResId: Int = R.layout.fragment_onboarding_field

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
