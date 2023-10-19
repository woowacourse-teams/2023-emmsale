package com.emmsale.presentation.ui.editMyProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentEditmyprofileClubsAddBottomDialogBinding
import com.emmsale.presentation.common.views.ActivityTag
import com.emmsale.presentation.common.views.activityChipOf
import com.emmsale.presentation.ui.editMyProfile.uiState.ActivityUiState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClubsAddBottomDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentEditmyprofileClubsAddBottomDialogBinding? = null
    private val binding
        get() = requireNotNull(_binding) {
            "[ERROR] 동아리 활동 추가 다이얼로그가 보이지 않을 때 바인딩 객체에 접근했습니다."
        }

    private val viewModel: EditMyProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditmyprofileClubsAddBottomDialogBinding
            .inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDataBinding()
        setupUiLogic()
        viewModel.fetchAllUnSelectedActivities()
    }

    override fun getTheme(): Int = R.style.RoundBottomSheetDialogStyle

    private fun initDataBinding() {
        binding.viewModel = viewModel
        binding.addClubs = ::addClubs
    }

    private fun addClubs() {
        viewModel.addSelectedClubs()
        dismiss()
    }

    private fun setupUiLogic() {
        setupClubsUiLogic()
    }

    private fun setupClubsUiLogic() {
        viewModel.activities.observe(viewLifecycleOwner) { allActivities ->
            binding.cgEditmyprofileclubsdialogClubs.removeAllViews()
            binding.cgEditmyprofileclubsdialogClubs.addChips(allActivities.clubs)
        }
    }

    private fun ChipGroup.addChips(clubs: List<ActivityUiState>) {
        clubs.forEach { club ->
            val chip = getActivityTag(club)
            addView(chip)
        }
    }

    private fun getActivityTag(club: ActivityUiState): ActivityTag {
        return activityChipOf {
            text = club.activity.name
            isChecked = club.isSelected
            setOnCheckedChangeListener { _, _ ->
                viewModel.toggleActivitySelection(club.activity.id)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG: String = ClubsAddBottomDialogFragment::class.java.simpleName
    }
}
