package com.emmsale.presentation.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.emmsale.R
import com.emmsale.databinding.ActivityOnboardingBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.ui.main.MainActivity
import com.emmsale.presentation.ui.onboarding.uiState.OnboardingUiEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity :
    NetworkActivity<ActivityOnboardingBinding>(R.layout.activity_onboarding) {

    override val viewModel: OnboardingViewModel by viewModels()

    private val fragmentStateAdapter: OnboardingFragmentStateAdapter by lazy {
        OnboardingFragmentStateAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupFragmentStateAdapter()
        setupBackPressedDispatcher()

        observeUiEvent()
    }

    private fun setupFragmentStateAdapter() {
        binding.vpOnboarding.adapter = fragmentStateAdapter
    }

    private fun setupBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateToPrevPage()
                }
            },
        )
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(this, ::handleUiEvent)
    }

    private fun handleUiEvent(uiEvent: OnboardingUiEvent) {
        when (uiEvent) {
            OnboardingUiEvent.FieldLimitExceedChecked -> binding.root.showSnackBar(R.string.onboardingfield_selection_limit_exceed)
            OnboardingUiEvent.JoinComplete -> navigateToMain()
            OnboardingUiEvent.JoinFail -> binding.root.showSnackBar(getString(R.string.onboarding_join_failed_message))
        }
    }

    private fun navigateToMain() {
        MainActivity.startActivity(this)
        finish()
    }

    fun navigateToNextPage() {
        val currentPage = binding.vpOnboarding.currentItem
        val lastPage = fragmentStateAdapter.itemCount - 1
        when {
            currentPage < lastPage -> binding.vpOnboarding.currentItem += 1
            currentPage == lastPage -> viewModel.join()
        }
    }

    fun navigateToPrevPage() {
        when {
            binding.vpOnboarding.currentItem == 0 -> finish()
            binding.vpOnboarding.currentItem > 0 -> binding.vpOnboarding.currentItem -= 1
        }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, OnboardingActivity::class.java)
            context.startActivity(intent)
        }
    }
}
