package com.emmsale.presentation.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModelProvider
import com.emmsale.BuildConfig
import com.emmsale.R
import com.emmsale.databinding.ActivityLoginBinding
import com.emmsale.presentation.ui.login.uistate.LoginUiState
import com.emmsale.presentation.utils.binding.setContentView
import com.emmsale.presentation.utils.builder.uri
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this, LoginViewModelFactory(this))[LoginViewModel::class.java]
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater).setContentView(this)
        binding.viewModel = viewModel
        binding.btnLogin.setOnClickListener(this)

        viewModel.loginState.observe(this) { loginState ->
            when (loginState) {
                is LoginUiState.Login -> navigateToMain()
                is LoginUiState.Register -> navigateToOnboarding()
                is LoginUiState.Loading -> changeLoadingVisibility(true)
                is LoginUiState.Error -> showLoginFailedMessage()
            }
        }
    }

    private fun navigateToMain() {
        // startActivity(MainActivity.getIntent(this))
        finish()
    }

    private fun navigateToOnboarding() {
        // startActivity(OnboardingActivity.getIntent(this))
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
        Snackbar.make(binding.root, "로그인에 실패했어요 \uD83D\uDE25", Snackbar.LENGTH_SHORT).show()
    }

    private fun changeLoadingVisibility(isShow: Boolean) {
        when (isShow) {
            true -> binding.pbLogin.visibility = View.VISIBLE
            false -> binding.pbLogin.visibility = View.GONE
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_login -> navigateToGithubLogin()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.getQueryParameter(GITHUB_CODE_PARAMETER)?.let(viewModel::saveGithubCode)
    }

    companion object {
        private const val GITHUB_CODE_PARAMETER = "code"
    }
}
