package com.emmsale.presentation.ui.notificationPageList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.databinding.ActivityNotificationBoxBinding

class NotificationBoxActivity : AppCompatActivity() {
    private val binding by lazy { ActivityNotificationBoxBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        initNotificationBoxTabLayout()
        initBackPressNavigationClickListener()
    }

    private fun initNotificationBoxTabLayout() {
        initNotificationBoxTabMediator()
    }

    private fun initNotificationBoxTabMediator() {
        binding.vpNotificationBox.adapter = NotificationBoxFragmentStateAdapter(this)
    }

    private fun initBackPressNavigationClickListener() {
        binding.tbNotiBox.setNavigationOnClickListener { finish() }
    }

    companion object {
        fun getIntent(context: Context): Intent =
            Intent(context, NotificationBoxActivity::class.java)

        fun startActivity(context: Context) {
            context.startActivity(getIntent(context))
        }
    }
}
