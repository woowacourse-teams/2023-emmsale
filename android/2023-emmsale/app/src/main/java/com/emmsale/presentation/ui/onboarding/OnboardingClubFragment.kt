package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingClubBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.views.activityChipOf
import com.emmsale.presentation.ui.onboarding.uiState.ActivityUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingClubFragment :
    BaseFragment<FragmentOnboardingClubBinding>(R.layout.fragment_onboarding_club) {

    val viewModel: OnboardingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDataBinding()
        setupToolbar()

        observeClubs()
    }

    private fun setupDataBinding() {
        binding.viewModel = viewModel
        binding.onNextButtonClick =
            { (requireActivity() as OnboardingActivity).navigateToNextPage() }
    }

    private fun setupToolbar() {
        binding.tbClubFragment.setNavigationOnClickListener {
            (requireActivity() as OnboardingActivity).onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeClubs() {
        viewModel.clubs.observe(viewLifecycleOwner) { clubs ->
            binding.chipgroupClubTags.removeAllViews()
            clubs.forEach(::addClubChip)
        }
    }

    private fun addClubChip(clubTag: ActivityUiState) {
        binding.chipgroupClubTags.addView(createChip(clubTag))
    }

    private fun createChip(activity: ActivityUiState) = activityChipOf {
        text = activity.activity.name
        isChecked = activity.isSelected
        setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateSelection(activity.activity.id, isChecked)
        }
    }
}
