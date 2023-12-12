package com.emmsale.presentation.ui.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabsIntent
import com.emmsale.BuildConfig
import com.emmsale.R
import com.emmsale.databinding.ActivityLoginBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.extension.checkPostNotificationPermission
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegate
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegateImpl
import com.emmsale.presentation.ui.login.uiState.LoginUiEvent
import com.emmsale.presentation.ui.main.MainActivity
import com.emmsale.presentation.ui.onboarding.OnboardingActivity
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity :
    NetworkActivity<ActivityLoginBinding>(R.layout.activity_login),
    FirebaseAnalyticsDelegate by FirebaseAnalyticsDelegateImpl("login") {

    override val viewModel: LoginViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) showToast(getString(R.string.login_post_notification_permission_granted_message))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        registerScreen(this)

        setupDataBinding()

        observeUiEvent()

        askNotificationPermission()
    }

    private fun setupDataBinding() {
        binding.viewModel = viewModel
        binding.onLoginButtonClick = ::navigateToGithubLogin
    }

    private fun navigateToGithubLogin() {
        val customTabIntent = CustomTabsIntent.Builder().build()
        customTabIntent.launchUrl(this, createGithubLoginUri())
    }

    private fun createGithubLoginUri() = Uri.Builder()
        .scheme("https")
        .authority("github.com")
        .appendPath("login")
        .appendPath("oauth")
        .appendPath("authorize")
        .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
        .build()

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(this, ::handleUiEvent)
    }

    private fun handleUiEvent(uiEvent: LoginUiEvent) {
        when (uiEvent) {
            LoginUiEvent.JoinComplete -> {
                OnboardingActivity.startActivity(this)
                finish()
            }

            LoginUiEvent.LoginComplete -> {
                MainActivity.startActivity(this)
                finish()
            }

            LoginUiEvent.LoginFail -> binding.root.showSnackBar(getString(R.string.login_failed_message))
        }
    }

    @SuppressLint("InlinedApi")
    private fun askNotificationPermission() {
        if (checkPostNotificationPermission()) return
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        githubLogin(intent)
    }

    private fun githubLogin(intent: Intent?) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
            viewModel.login(
                fcmToken = fcmToken,
                code = intent?.parseGithubCode() ?: "",
            )
        }
    }

    private fun Intent.parseGithubCode(): String? = data?.getQueryParameter(GITHUB_CODE_PARAMETER)

    companion object {
        private const val GITHUB_CODE_PARAMETER = "code"

        fun startActivity(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }
}
