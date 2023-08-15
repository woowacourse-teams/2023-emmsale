package com.emmsale.presentation.ui.setting.notificationConfig

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R

class NotificationConfigActivity : AppCompatActivity() {
    private val viewModel: NotificationConfigViewModel by viewModels { NotificationConfigViewModel.factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_config)
    }
}