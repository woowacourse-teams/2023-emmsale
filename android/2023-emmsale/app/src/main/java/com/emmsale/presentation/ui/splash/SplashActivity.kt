package com.emmsale.presentation.ui.splash

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.window.SplashScreenView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.emmsale.R
import com.emmsale.presentation.ui.login.LoginActivity
import com.emmsale.presentation.ui.main.MainActivity
import com.emmsale.presentation.ui.splash.uistate.SplashUiState

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels { SplashViewModel.factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setupSplashObserver()
    }

    private fun setupSplashObserver() {
        viewModel.splashState.observe(this) { splashState ->
            when (splashState) {
                is SplashUiState.Loading -> initSplashAnimation(splashState.splashTimeMs)
                is SplashUiState.Done -> handleAutoLogin(splashState.isAutoLogin)
            }
        }
    }

    private fun initSplashAnimation(durationMs: Long) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            showSplashEndAnimation(splashScreenView, durationMs)
        }
    }

    private fun showSplashEndAnimation(splashScreenView: SplashScreenView, durationMs: Long) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return

        ObjectAnimator.ofFloat(splashScreenView, View.ALPHA, 1F, 0F).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = durationMs
            doOnEnd { splashScreenView.remove() }
        }.start()
    }

    private fun handleAutoLogin(isAutoLogin: Boolean) {
        if (isAutoLogin) {
            navigateToMainScreen()
        } else {
            navigateToLoginScreen()
        }
    }

    private fun navigateToMainScreen() {
        MainActivity.startActivity(this)
        finish()
    }

    private fun navigateToLoginScreen() {
        LoginActivity.startActivity(this)
        finish()
    }
}
