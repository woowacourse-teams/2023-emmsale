package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingClubBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.common.views.activityChipOf
import com.emmsale.presentation.ui.onboarding.uistate.ActivityUiState

class OnboardingClubFragment : BaseFragment<FragmentOnboardingClubBinding>(), View.OnClickListener {
    val viewModel: OnboardingViewModel by activityViewModels { OnboardingViewModel.factory }
    override val layoutResId: Int = R.layout.fragment_onboarding_club

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initClickListener()
        setupClubs()
    }

    private fun initClickListener() {
        binding.btnNext.setOnClickListener(this)
    }

    private fun setupClubs() {
        viewModel.clubs.observe(viewLifecycleOwner) { clubs ->
            clubs?.activities?.forEach(::addClubChip)
        }
    }

    private fun addClubChip(clubTag: ActivityUiState) {
        binding.chipgroupClubTags.addView(createChip(clubTag))
    }

    private fun createChip(clubTag: ActivityUiState) = activityChipOf {
        text = clubTag.name
        isChecked = clubTag.isSelected
        setOnCheckedChangeListener { _, _ -> viewModel.toggleTagSelection(clubTag) }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_next -> (requireActivity() as OnboardingActivity).navigateToNextPage()
        }
    }
}
