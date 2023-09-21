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
class OnboardingClubFragment : BaseFragment<FragmentOnboardingClubBinding>(), View.OnClickListener {
    val viewModel: OnboardingViewModel by activityViewModels()
    override val layoutResId: Int = R.layout.fragment_onboarding_club

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initClickListener()
        setupClubs()
    }

    private fun initClickListener() {
        binding.btnNext.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
    }

    private fun setupClubs() {
        viewModel.activities.observe(viewLifecycleOwner) { activities ->
            binding.chipgroupClubTags.removeAllViews()
            activities.clubs.forEach(::addClubChip)
        }
    }

    private fun addClubChip(clubTag: ActivityUiState) {
        binding.chipgroupClubTags.addView(createChip(clubTag))
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
