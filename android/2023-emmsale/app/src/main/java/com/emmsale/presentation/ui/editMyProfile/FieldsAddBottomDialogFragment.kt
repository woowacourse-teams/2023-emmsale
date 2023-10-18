package com.emmsale.presentation.ui.editMyProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentEditmyprofileFieldsAddBottomDialogBinding
import com.emmsale.presentation.common.views.ActivityTag
import com.emmsale.presentation.common.views.activityChipOf
import com.emmsale.presentation.ui.editMyProfile.uiState.ActivityUiState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FieldsAddBottomDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentEditmyprofileFieldsAddBottomDialogBinding? = null
    private val binding
        get() = requireNotNull(_binding) { "[ERROR] 관심 카테고리 추가 다이얼로그가 보이지 않을 때 바인딩 객체에 접근했습니다." }

    private val viewModel: EditMyProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditmyprofileFieldsAddBottomDialogBinding
            .inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDataBinding()
        setupUiLogic()
        viewModel.fetchAllActivities()
    }

    override fun getTheme(): Int = R.style.RoundBottomSheetDialogStyle

    private fun initDataBinding() {
        binding.viewModel = viewModel
        binding.addFields = ::addFields
    }

    private fun addFields() {
        viewModel.addSelectedFields()
        dismiss()
    }

    private fun setupUiLogic() {
        setupFieldsUiLogic()
    }

    private fun setupFieldsUiLogic() {
        viewModel.activities.observe(viewLifecycleOwner) { allActivities ->
            binding.cgEditmyprofilefieldsdialogFields.removeAllViews()
            binding.cgEditmyprofilefieldsdialogFields.addChips(allActivities.clubs)
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
        val TAG: String = FieldsAddBottomDialogFragment::class.java.simpleName
    }
}
