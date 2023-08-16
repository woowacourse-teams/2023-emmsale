package com.emmsale.presentation.ui.setting.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityNotificationConfigBinding
import com.emmsale.presentation.ui.setting.notificationTagConfig.NotificationTagConfigActivity

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
        initTagAddButtonClickListener()
    }

    private fun initToolbarMenuClickListener() {
        binding.tbNotificationConfig.setOnMenuItemClickListener {
            if (it.itemId == R.id.close) finish()
            true
        }
    }

    private fun initTagAddButtonClickListener() {
        binding.btnTagAdd.setOnClickListener {
            navigateToNotificationTagConfigActivity()
        }
    }

    private fun navigateToNotificationTagConfigActivity() {
        NotificationTagConfigActivity.startActivity(this)
    }

    companion object {
        fun getIntent(context: Context): Intent =
            Intent(context, NotificationConfigActivity::class.java)
    }
}
