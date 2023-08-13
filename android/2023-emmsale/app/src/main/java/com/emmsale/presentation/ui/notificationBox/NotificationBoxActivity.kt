package com.emmsale.presentation.ui.notificationBox

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityNotificationBoxBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

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
        initNotificationBoxTabLayoutSelectedListener()
        initNotificationBoxTabMediator()
    }

    private fun initNotificationBoxTabLayoutSelectedListener() {
        binding.tlNotificationBox.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.vpNotificationBox.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun initNotificationBoxTabMediator() {
        val notificationBoxTabNames = listOf(
            getString(R.string.notificationbox_recruitment),
            getString(R.string.notificationbox_primary),
        )

        binding.vpNotificationBox.adapter = NotificationBoxFragmentStateAdapter(this)
        TabLayoutMediator(binding.tlNotificationBox, binding.vpNotificationBox) { tab, position ->
            tab.text = notificationBoxTabNames[position]
        }.attach()
    }

    private fun initBackPressNavigationClickListener() {
        binding.tbNotiBox.setNavigationOnClickListener { finish() }
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, NotificationBoxActivity::class.java))
        }
    }
}
