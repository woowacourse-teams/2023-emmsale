package com.emmsale.presentation.ui.login

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityLoginBinding
import com.emmsale.presentation.common.extension.checkPostNotificationPermission
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.login.uistate.LoginUiState
import com.emmsale.presentation.ui.onboarding.OnboardingActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels { LoginViewModel.factory }
    private lateinit var binding: ActivityLoginBinding

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) showToast(getString(R.string.post_notification_permission_granted_message))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewModel = viewModel

        viewModel.loginState.observe(this) { loginState ->
            when (loginState) {
                is LoginUiState.Login -> navigateToMain()
                is LoginUiState.Register -> navigateToOnboarding()
                is LoginUiState.Loading -> changeLoadingVisibility(true)
                is LoginUiState.Error -> showLoginFailedMessage()
            }
        }

        binding.btnLogin.setOnClickListener {
            FirebaseMessaging.getInstance().token.addOnSuccessListener(viewModel::login)
        }
        askNotificationPermission()
    }

    private fun navigateToMain() {
        // startActivity(MainActivity.getIntent(this))
        finish()
    }

    private fun navigateToOnboarding() {
        OnboardingActivity.startActivity(this)
        finish()
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

    @SuppressLint("InlinedApi")
    private fun askNotificationPermission() {
        if (checkPostNotificationPermission()) return
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
