package com.emmsale.presentation.ui.main.myProfile.editMyProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.emmsale.databinding.FragmentEditmyprofileClubsAddBottomDialogBinding
import com.emmsale.presentation.common.views.activityChipOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ClubsAddBottomDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentEditmyprofileClubsAddBottomDialogBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("동아리 활동 추가 다이얼로그가 보이지 않을 때 바인딩 객체에 접근했습니다.")

    private val viewModel: EditMyProfileViewModel by activityViewModels { EditMyProfileViewModel.factory }

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
    }

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
        viewModel.selectableClubs.observe(viewLifecycleOwner) {
            binding.cgEditmyprofileclubsdialogClubs.removeAllViews()
            it.forEach { club ->
                binding.cgEditmyprofileclubsdialogClubs.addView(
                    activityChipOf {
                        text = club.name
                        isChecked = club.isSelected
                        setOnCheckedChangeListener { _, isChecked ->
                            viewModel.setClubSelection(club.id, isChecked)
                        }
                    },
                )
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
