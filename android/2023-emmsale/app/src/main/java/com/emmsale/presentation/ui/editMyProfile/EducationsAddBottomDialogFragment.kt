package com.emmsale.presentation.ui.editMyProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.data.model.Activity
import com.emmsale.databinding.FragmentEditmyprofileEducationsAddBottomDialogBinding
import com.emmsale.presentation.common.views.ActivityTag
import com.emmsale.presentation.common.views.activityChipOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EducationsAddBottomDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentEditmyprofileEducationsAddBottomDialogBinding? = null
    private val binding
        get() = requireNotNull(_binding) { "[ERROR] 교육 활동 추가 다이얼로그가 보이지 않을 때 바인딩 객체에 접근했습니다." }

    private val viewModel: EditMyProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditmyprofileEducationsAddBottomDialogBinding
            .inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDataBinding()
        setupUiLogic()
    }

    override fun getTheme(): Int = R.style.RoundBottomSheetDialogStyle

    private fun initDataBinding() {
        binding.viewModel = viewModel
        binding.addEducations = ::addEducations
    }

    private fun addEducations() {
        viewModel.addSelectedEducations()
        dismiss()
    }

    private fun setupUiLogic() {
        setupEducationsUiLogic()
    }

    private fun setupEducationsUiLogic() {
        viewModel.educations.observe(viewLifecycleOwner) { educations ->
            binding.cgEditmyprofileeducationsdialogEducations.removeAllViews()
            binding.cgEditmyprofileeducationsdialogEducations.addChips(educations)
        }
    }

    private fun ChipGroup.addChips(educations: List<Activity>) {
        educations.forEach { education ->
            val chip = getActivityTag(education)
            addView(chip)
        }
    }

    private fun getActivityTag(education: Activity): ActivityTag {
        return activityChipOf {
            text = education.name
            isChecked = education in viewModel.selectedEducations.value!!
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.setActivitySelection(education.id, isChecked)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG: String = EducationsAddBottomDialogFragment::class.java.simpleName
    }
}
