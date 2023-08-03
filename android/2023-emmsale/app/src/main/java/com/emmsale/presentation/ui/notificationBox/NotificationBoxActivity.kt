package com.emmsale.presentation.ui.notificationBox

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.databinding.ActivityNotificationBoxBinding
import com.emmsale.presentation.ui.notificationBox.recyclerview.NotificationBoxAdapter

class NotificationBoxActivity : AppCompatActivity() {
    private val viewModel: NotificationBoxViewModel by viewModels { NotificationBoxViewModel.factory }
    private val binding: ActivityNotificationBoxBinding by lazy {
        ActivityNotificationBoxBinding.inflate(layoutInflater)
    }
    private val notificationBoxAdapter: NotificationBoxAdapter by lazy {
        NotificationBoxAdapter(
            onClickNotification = ::navigateToNotificationDetail,
            onDelete = viewModel::deleteNotification,
        )
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
            notificationBoxAdapter.submitList(uiState.notifications)
        }
    }

    private fun initView() {
        binding.rvNotiBox.adapter = notificationBoxAdapter
        binding.rvNotiBox.setHasFixedSize(true)
    }

    private fun navigateToNotificationDetail(notificationId: Long, otherUid: Long) {
        // TODO: 상대방 uid를 바탕으로, Notification 상세 화면 조회
        // TODO: 다이얼로그로 띄우기
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, NotificationBoxActivity::class.java))
        }
    }
}
