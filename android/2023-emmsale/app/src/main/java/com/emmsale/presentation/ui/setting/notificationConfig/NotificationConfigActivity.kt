package com.emmsale.presentation.ui.setting.notificationConfig

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityNotificationConfigBinding

class NotificationConfigActivity : AppCompatActivity() {
    private val viewModel: NotificationConfigViewModel by viewModels { NotificationConfigViewModel.factory }
    private val binding: ActivityNotificationConfigBinding by lazy {
        ActivityNotificationConfigBinding.inflate(layoutInflater)
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
        initToolbarMenuClickListener()
        initNotificationSwitchClickListener()
        initTagAddButtonClickListener()
    }

    private fun initToolbarMenuClickListener() {
        binding.tbNotificationConfig.setOnMenuItemClickListener {
            if (it.itemId == R.id.close) finish()
            true
        }
    }

    private fun initNotificationSwitchClickListener() {
        binding.switchNotificationReceiveConfig.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNotificationReceiveConfig(isChecked)
        }
    }

    private fun initTagAddButtonClickListener() {
        binding.btnTagAdd.setOnClickListener {
            navigateToNotificationTagConfigActivity()
        }
    }

    private fun navigateToNotificationTagConfigActivity() {

    }
}