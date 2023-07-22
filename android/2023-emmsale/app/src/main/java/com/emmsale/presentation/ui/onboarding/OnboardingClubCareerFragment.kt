package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingClubCareerBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.common.viewModelFactory
import com.emmsale.presentation.common.views.chipOf
import com.emmsale.presentation.ui.onboarding.uistate.CareerContentUiState

class OnboardingClubCareerFragment :
    BaseFragment<FragmentOnboardingClubCareerBinding>() {
    override val viewModel: OnboardingViewModel by activityViewModels { viewModelFactory }
    override val layoutResId: Int = R.layout.fragment_onboarding_club_career

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        setupClubs()
    }

    private fun setupClubs() {
        viewModel.clubs.observe(viewLifecycleOwner) { clubs ->
            clubs?.careerContents?.forEach(::addClubChip)
        }
    }

    private fun addClubChip(clubTag: CareerContentUiState) {
        binding.chipgroupClubTags.addView(createChip(clubTag))
    }

    private fun createChip(clubTag: CareerContentUiState) = chipOf {
        text = clubTag.name
        isChecked = clubTag.isSelected
        setOnCheckedChangeListener { _, _ -> viewModel.toggleClubTag(clubTag) }
    }
}
