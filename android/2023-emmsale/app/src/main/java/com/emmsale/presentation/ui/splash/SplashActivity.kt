package com.emmsale.presentation.ui.splash

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.window.SplashScreenView
import androidx.activity.ComponentActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.emmsale.R
import com.emmsale.data.repository.interfaces.ConfigRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.databinding.ActivitySplashBinding
import com.emmsale.presentation.common.extension.addListener
import com.emmsale.presentation.common.extension.isUpdateNeeded
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.views.ConfirmDialog
import com.emmsale.presentation.ui.login.LoginActivity
import com.emmsale.presentation.ui.main.MainActivity
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }

    @Inject
    lateinit var tokenRepository: TokenRepository

    @Inject
    lateinit var configRepository: ConfigRepository

    private val animationJob: Job =
        lifecycleScope.launch(start = CoroutineStart.LAZY) { delay(SPLASH_ANIMATION_DURATION) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) setContentView(binding.root)
        animationJob.start()
        checkAppUpdate()

        installSplashScreen()
        setupSplashAnimation()
    }

    private fun setupSplashAnimation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return

        splashScreen.setOnExitAnimationListener(::showSplashEndAnimation)
    }

    private fun showSplashEndAnimation(splashScreenView: SplashScreenView) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return

        ObjectAnimator.ofFloat(splashScreenView, View.ALPHA, 1F, 0F).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = SPLASH_ANIMATION_DURATION
            doOnEnd { splashScreenView.remove() }
        }.start()
    }

    private fun checkAppUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)

        appUpdateManager.appUpdateInfo.addListener(
            onSuccess = { info ->
                animationJob.invokeOnCompletion {
                    when {
                        info.isUpdateNeeded() -> showUpdateConfirmDialog()
                        canAutoLogin() -> navigateToMain()
                        else -> navigateToLogin()
                    }
                }
            },
            onFailed = {
                showToast(R.string.splash_not_installed_playstore)
                navigateToLogin()
            },
        )
    }

    private fun showUpdateConfirmDialog() {
        ConfirmDialog(
            this,
            title = getString(R.string.splash_app_update_title),
            message = getString(R.string.splash_app_update_message),
            onPositiveButtonClick = ::navigateToPlayStore,
            onNegativeButtonClick = {
                showToast(R.string.splash_app_update_canceled_message)
                finishAffinity()
            },
            cancelable = false,
        ).show()
    }

    private fun navigateToPlayStore() {
        val intent = Intent(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
        startActivity(intent)
    }

    private fun canAutoLogin(): Boolean =
        tokenRepository.getToken() != null && configRepository.getConfig().isAutoLogin

    private fun navigateToMain() {
        MainActivity.startActivity(this)
        finish()
    }

    private fun navigateToLogin() {
        LoginActivity.startActivity(this)
        finish()
    }

    companion object {
        private const val SPLASH_ANIMATION_DURATION = 1500L

        fun getIntent(context: Context): Intent = Intent(
            context,
            SplashActivity::class.java,
        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}
