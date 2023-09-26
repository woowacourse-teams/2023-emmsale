package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingNameBinding
import com.emmsale.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingNameFragment : BaseFragment<FragmentOnboardingNameBinding>(), View.OnClickListener {
    override val layoutResId: Int = R.layout.fragment_onboarding_name
    val viewModel: OnboardingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initClickListener()
    }

    private fun initClickListener() {
        binding.btnNext.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_next -> (requireActivity() as OnboardingActivity).navigateToNextPage()
        }
    }
}