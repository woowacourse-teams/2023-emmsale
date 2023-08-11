package com.emmsale.presentation.ui.notificationBox

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityNotificationBoxBinding
import com.emmsale.presentation.common.extension.showSnackbar
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.notificationBox.recyclerview.body.NotificationBodyClickListener
import com.emmsale.presentation.ui.notificationBox.recyclerview.header.NotificationBoxHeaderAdapter
import com.emmsale.presentation.ui.notificationBox.recyclerview.header.NotificationHeaderClickListener

class NotificationBoxActivity :
    AppCompatActivity(),
    NotificationHeaderClickListener,
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
        setupRecruitment()
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
        Log.d("NotificationBoxActivity", "$notificationId, $otherUid")
    }

    override fun onAccept(notificationId: Long) {
        viewModel.acceptRecruit(notificationId)
    }

    override fun onReject(notificationId: Long) {
        viewModel.rejectRecruit(notificationId)
    }

    override fun onToggleClick(eventId: Long) {
        viewModel.toggleExpand(eventId)
    }

    private fun setupNotifications() {
        viewModel.notifications.observe(this) { uiState ->
            when {
                uiState.isError -> showToast(getString(R.string.all_data_loading_failed_message))
                !uiState.isError && !uiState.isLoading ->
                    notificationBoxHeaderAdapter.submitList(uiState.notifications)
            }
        }
    }

    private fun setupRecruitment() {
        viewModel.recruitmentUiState.observe(this) { uiState ->
            when {
                uiState.isError -> showToast(getString(R.string.notificationbox_recruitment_status_changing_failed))
                uiState.isAccepted -> showRecruitmentAcceptedDialog()
                uiState.isRejected -> showRecruitmentRejectedMessage()
            }
        }
    }

    private fun showRecruitmentAcceptedDialog() {

    }

    private fun showRecruitmentRejectedMessage() {
        binding.root.showSnackbar(getString(R.string.notificationbox_recruitment_rejected_message))
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, NotificationBoxActivity::class.java))
        }
    }
}
