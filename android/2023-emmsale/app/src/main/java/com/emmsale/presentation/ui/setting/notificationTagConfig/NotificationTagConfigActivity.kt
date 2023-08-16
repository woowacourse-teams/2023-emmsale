package com.emmsale.presentation.ui.setting.notificationTagConfig

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.databinding.ActivityNotificationTagConfigBinding

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
    }
}
