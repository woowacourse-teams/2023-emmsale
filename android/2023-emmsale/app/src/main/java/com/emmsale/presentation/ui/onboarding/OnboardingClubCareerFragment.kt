package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingClubCareerBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.common.viewModelFactory
import com.emmsale.presentation.common.views.chipOf
import com.emmsale.presentation.ui.onboarding.uistate.CareerContentUiState
import com.google.android.material.snackbar.Snackbar

class OnboardingClubCareerFragment :
    BaseFragment<FragmentOnboardingClubCareerBinding>() {
    override val viewModel: OnboardingViewModel by activityViewModels { viewModelFactory }
    override val layoutResId: Int = R.layout.fragment_onboarding_club_career

    private lateinit var clubCareerAdapter: CareerSpinnerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        setupClubSpinner()
        setupClubs()
        setupSelectedClubs()
    }

    private fun setupClubSpinner() {
        clubCareerAdapter = CareerSpinnerAdapter(mutableListOf())
        binding.spinnerClubCareers.adapter = clubCareerAdapter
        binding.spinnerClubCareers.onItemSelectedListener = ClubSpinnerSelectedListener()
    }

    private fun setupClubs() {
        viewModel.clubs.observe(viewLifecycleOwner) { clubs ->
            clubs?.run { updateClubSpinner(this.careerContentsWithCategory) }
        }
    }

    private fun setupSelectedClubs() {
        viewModel.selectedClubTags.observe(viewLifecycleOwner) { clubs ->
            binding.chipgroupClubTags.removeAllViews()
            clubs.forEach(::addClubChip)
        }
    }

    private fun addClubChip(clubTag: CareerContentUiState) {
        binding.chipgroupClubTags.addView(
            chipOf(requireContext()) {
                text = clubTag.name
                setOnCloseIconClickListener { viewModel.removeClubTag(clubTag) }
            }
        )
    }

    private fun updateClubSpinner(clubTags: List<CareerContentUiState>) {
        clubCareerAdapter.updateItems(clubTags)
    }

    private fun showLoginFailedMessage() {
        Snackbar.make(binding.root, "데이터를 불러오기에 실패했어요 \uD83D\uDE25", Snackbar.LENGTH_SHORT)
            .show()
    }

    inner class ClubSpinnerSelectedListener : OnItemSelectedListener {

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            if (position == 0) return
            viewModel.addClubTag(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}
