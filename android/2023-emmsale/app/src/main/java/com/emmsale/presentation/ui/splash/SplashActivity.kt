package com.emmsale.presentation.ui.splash

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.window.SplashScreenView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.emmsale.R
import com.emmsale.databinding.ActivitySplashBinding
import com.emmsale.presentation.common.extension.addListener
import com.emmsale.presentation.common.extension.isUpdateNeeded
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.views.ConfirmDialog
import com.emmsale.presentation.ui.login.LoginActivity
import com.emmsale.presentation.ui.main.MainActivity
import com.emmsale.presentation.ui.splash.uiState.SplashUiState
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    private val viewModel: SplashViewModel by viewModels()
    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupSplashObserver()
    }

    private fun setupSplashObserver() {
        viewModel.splash.observe(this) { splashState ->
            when (splashState) {
                is SplashUiState.Loading -> initSplashAnimation(splashState.splashTimeMs)
                // is SplashUiState.Done -> checkAppUpdate(splashState.isAutoLogin)
                is SplashUiState.Done -> navigateToLoginScreen()
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

    private fun checkAppUpdate(isAutoLogin: Boolean) {
        val appUpdateManager = AppUpdateManagerFactory.create(this)

        appUpdateManager.appUpdateInfo.addListener(
            onSuccess = { updateInfo -> updateInfo.handleUpdateInfo(isAutoLogin) },
            onFailed = { showToast(R.string.all_network_error_title) },
        )
    }

    private fun AppUpdateInfo.handleUpdateInfo(isAutoLogin: Boolean) {
        when {
            isUpdateNeeded() -> confirmUpdate()
            else -> navigateToNextScreen(isAutoLogin)
        }
    }

    private fun confirmUpdate() {
        ConfirmDialog(
            this,
            title = getString(R.string.splash_app_update_title),
            message = getString(R.string.splash_app_update_message),
            onPositiveButtonClick = ::navigateToAppStore,
            onNegativeButtonClick = {
                showToast(R.string.splash_app_update_canceled_message)
                finishAffinity()
            },
        ).show()
    }

    private fun navigateToAppStore() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        finish()
    }

    private fun navigateToNextScreen(isAutoLogin: Boolean) {
        when {
            isAutoLogin -> navigateToMainScreen()
            else -> navigateToLoginScreen()
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
