package com.emmsale.presentation.ui.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.emmsale.BuildConfig
import com.emmsale.R
import com.emmsale.databinding.ActivityLoginBinding
import com.emmsale.presentation.common.extension.checkPostNotificationPermission
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegate
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegateImpl
import com.emmsale.presentation.ui.login.uiState.LoginUiState
import com.emmsale.presentation.ui.main.MainActivity
import com.emmsale.presentation.ui.onboarding.OnboardingActivity
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity :
    AppCompatActivity(),
    FirebaseAnalyticsDelegate by FirebaseAnalyticsDelegateImpl("login") {
    private val viewModel: LoginViewModel by viewModels()
    private val binding: ActivityLoginBinding by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) showToast(getString(R.string.login_post_notification_permission_granted_message))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        registerScreen(this)
        binding.viewModel = viewModel
        setupClickListener()
        setupLoginState()
        askNotificationPermission()
    }

    private fun setupClickListener() {
        setupGithubLoginClickListener()
    }

    private fun setupGithubLoginClickListener() {
        binding.btnGithubLogin.setOnClickListener {
            navigateToGithubLogin()
        }
    }

    private fun setupLoginState() {
        viewModel.loginState.observe(this) { loginState ->
            when (loginState) {
                is LoginUiState.Login -> navigateToMain()
                is LoginUiState.Onboarded -> navigateToOnboarding()
                is LoginUiState.Loading -> changeLoadingVisibility(true)
                is LoginUiState.Error -> showLoginFailedMessage()
            }
        }
    }

    private fun navigateToMain() {
        MainActivity.startActivity(this)
        finish()
    }

    private fun navigateToOnboarding() {
        OnboardingActivity.startActivity(this)
        finish()
    }

    private fun navigateToGithubLogin() {
        val customTabIntent = CustomTabsIntent.Builder().build()
        customTabIntent.launchUrl(this, getGithubLoginUri())
    }

    private fun getGithubLoginUri(): Uri = uri {
        scheme("https")
        authority("github.com")
        appendPath("login")
        appendPath("oauth")
        appendPath("authorize")
        appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
    }

    private fun showLoginFailedMessage() {
        changeLoadingVisibility(false)
        binding.root.showSnackBar(getString(R.string.login_failed_message))
    }

    private fun changeLoadingVisibility(isShow: Boolean) {
        when (isShow) {
            true -> binding.pbLogin.visibility = View.VISIBLE
            false -> binding.pbLogin.visibility = View.GONE
        }
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

    private fun uri(block: Uri.Builder.() -> Unit): Uri = Uri.Builder().apply(block).build()

    private fun Intent.parseGithubCode(): String? = data?.getQueryParameter(GITHUB_CODE_PARAMETER)

    companion object {
        private const val GITHUB_CODE_PARAMETER = "code"

        fun startActivity(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    @SuppressLint("InlinedApi")
    private fun askNotificationPermission() {
        if (checkPostNotificationPermission()) return
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
