package com.emmsale.presentation.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private val fragmentStateAdapter: OnboardingFragmentStateAdapter by lazy {
        OnboardingFragmentStateAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFragmentStateAdapter()
        initBackPressedDispatcher()
    }

    private fun initFragmentStateAdapter() {
        binding.vpOnboarding.adapter = fragmentStateAdapter
    }

    private fun initBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(this, OnboardingOnBackPressedCallback())
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, OnboardingActivity::class.java)
            context.startActivity(intent)
        }
    }

    inner class OnboardingOnBackPressedCallback : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when {
                binding.vpOnboarding.currentItem == 0 -> finish()
                binding.vpOnboarding.currentItem > 0 -> binding.vpOnboarding.currentItem -= 1
            }
        }
    }
}
