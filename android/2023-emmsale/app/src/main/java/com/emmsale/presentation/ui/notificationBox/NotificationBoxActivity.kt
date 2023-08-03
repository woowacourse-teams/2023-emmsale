package com.emmsale.presentation.ui.notificationBox

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.databinding.ActivityNotificationBoxBinding
import com.emmsale.presentation.ui.notificationBox.recyclerview.body.NotificationBodyClickListener
import com.emmsale.presentation.ui.notificationBox.recyclerview.header.NotificationBoxHeaderAdapter
import com.emmsale.presentation.ui.notificationBox.recyclerview.header.NotificationHeaderClickListener

class NotificationBoxActivity : AppCompatActivity(), NotificationHeaderClickListener,
    NotificationBodyClickListener {
    private val viewModel: NotificationBoxViewModel by viewModels { NotificationBoxViewModel.factory }
    private val binding: ActivityNotificationBoxBinding by lazy {
        ActivityNotificationBoxBinding.inflate(layoutInflater)
    }
    private val notificationBoxHeaderAdapter: NotificationBoxHeaderAdapter by lazy {
        NotificationBoxHeaderAdapter(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        setupNotifications()
    }

    private fun setupNotifications() {
        viewModel.fetchNotifications()
        viewModel.notifications.observe(this) { uiState ->
            notificationBoxHeaderAdapter.submitList(uiState.notifications)
        }
    }

    private fun initView() {
        initNotificationBoxRecyclerView()
        initNavigationClickListener()
    }

    private fun initNotificationBoxRecyclerView() {
        binding.rvNotiBox.adapter = notificationBoxHeaderAdapter
        binding.rvNotiBox.setHasFixedSize(true)
    }

    private fun initNavigationClickListener() {
        binding.tbNotiBox.setNavigationOnClickListener { finish() }
    }

    override fun onClickBody(notificationId: Long, otherUid: Long) {
        navigateToNotificationDetail(notificationId, otherUid)
    }

    private fun navigateToNotificationDetail(notificationId: Long, otherUid: Long) {
        // TODO: 상대방 uid를 바탕으로, Notification 상세 화면 조회
        // TODO: 다이얼로그로 띄우기
    }

    override fun onAccept(notificationId: Long) {
        viewModel.acceptCompanion(notificationId)
    }

    override fun onReject(notificationId: Long) {
        viewModel.rejectCompanion(notificationId)
    }

    override fun onToggleClick(eventId: Long) {
        viewModel.toggleExpand(eventId)
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, NotificationBoxActivity::class.java))
        }
    }
}
