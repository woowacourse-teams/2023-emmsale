package com.emmsale.presentation.ui.main.myProfile.editMyProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.emmsale.databinding.FragmentEditmyprofileFieldsAddBottomDialogBinding
import com.emmsale.presentation.common.views.activityChipOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FieldsAddBottomDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentEditmyprofileFieldsAddBottomDialogBinding? = null
    private val binding
        get() =
            _binding ?: throw IllegalStateException("관심 카테고리 추가 다이얼로그가 보이지 않을 때 바인딩 객체에 접근했습니다.")

    private val viewModel: EditMyProfileViewModel by activityViewModels {
        EditMyProfileViewModel.factory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditmyprofileFieldsAddBottomDialogBinding
            .inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDataBinding()
        setupUiLogic()

        viewModel.fetchFields()
    }

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
        viewModel.selectableFields.observe(viewLifecycleOwner) {
            binding.cgEditmyprofilefieldsdialogFields.removeAllViews()
            it.forEach { field ->
                binding.cgEditmyprofilefieldsdialogFields.addView(
                    activityChipOf {
                        text = field.name
                        isChecked = field.isSelected
                        setOnCheckedChangeListener { _, isChecked ->
                            viewModel.setFieldSelection(field.id, isChecked)
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
        val TAG: String = FieldsAddBottomDialogFragment::class.java.simpleName
    }
}
