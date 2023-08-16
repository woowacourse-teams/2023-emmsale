package com.emmsale.presentation.ui.setting.notificationTagConfig

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityNotificationTagConfigBinding
import com.emmsale.presentation.common.views.ConfirmDialog

class NotificationTagConfigActivity : AppCompatActivity() {
    private val viewModel: NotificationTagConfigViewModel by viewModels { NotificationTagConfigViewModel.factory }
    private val binding: ActivityNotificationTagConfigBinding by lazy {
        ActivityNotificationTagConfigBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        initClickListener()
    }

    private fun initClickListener() {
        initToolbarNavigationClickListener()
    }

    private fun initToolbarNavigationClickListener() {
        binding.tbNotificationTagConfig.setNavigationOnClickListener {
            showFinishConfirmDialog()
        }
    }

    private fun showFinishConfirmDialog() {
        ConfirmDialog(
            context = this,
            title = getString(R.string.notificationtagconfig_finish_dialog_title),
            message = getString(R.string.notificationtagconfig_finish_dialog_message),
            onPositiveButtonClick = { finish() },
        ).show()
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, NotificationTagConfigActivity::class.java))
        }
    }
}
