package com.emmsale.presentation.ui.onboarding.name

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingNameBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.ui.onboarding.OnboardingActivity
import com.emmsale.presentation.ui.onboarding.OnboardingViewModel

class OnboardingNameFragment : BaseFragment<FragmentOnboardingNameBinding>(), View.OnClickListener {
    val viewModel: OnboardingViewModel by activityViewModels { OnboardingViewModel.factory }
    override val layoutResId: Int = R.layout.fragment_onboarding_name

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
