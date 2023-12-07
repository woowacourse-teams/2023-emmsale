package com.emmsale.presentation.ui.notificationTagConfig

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.emmsale.R
import com.emmsale.databinding.ActivityNotificationTagConfigBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegate
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegateImpl
import com.emmsale.presentation.common.views.ActivityTag
import com.emmsale.presentation.common.views.ConfirmDialog
import com.emmsale.presentation.common.views.activityChipOf
import com.emmsale.presentation.ui.notificationTagConfig.uiState.NotificationTagConfigUiEvent
import com.emmsale.presentation.ui.notificationTagConfig.uiState.NotificationTagConfigUiState
import com.emmsale.presentation.ui.notificationTagConfig.uiState.NotificationTagsConfigUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationTagConfigActivity :
    NetworkActivity<ActivityNotificationTagConfigBinding>(R.layout.activity_notification_tag_config),
    FirebaseAnalyticsDelegate by FirebaseAnalyticsDelegateImpl("notification_tag_config") {

    override val viewModel: NotificationTagConfigViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        registerScreen(this)

        setupDateBinding()
        setupBackPressedDispatcher()
        setupToolbar()

        observeNotificationTags()
        observeUiEvent()
    }

    private fun setupDateBinding() {
        binding.viewModel = viewModel
    }

    private fun setupBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewModel.isChanged.value == true) showFinishConfirmDialog() else finish()
                }
            },
        )
    }

    private fun setupToolbar() {
        binding.tbNotificationTagConfig.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun showFinishConfirmDialog() {
        ConfirmDialog(
            context = this,
            title = getString(R.string.notificationtagconfig_finish_dialog_title),
            message = getString(R.string.notificationtagconfig_finish_dialog_message),
            onPositiveButtonClick = { finish() },
        ).show()
    }

    private fun observeNotificationTags() {
        viewModel.notificationTags.observe(this, ::updateNotificationTagViews)
    }

    private fun updateNotificationTagViews(uiState: NotificationTagsConfigUiState) {
        binding.cgNotificationTag.removeAllViews()
        addEventTags(uiState.eventTags)
    }

    private fun addEventTags(eventTags: List<NotificationTagConfigUiState>) {
        eventTags.forEach(::addEventTag)
    }

    private fun addEventTag(eventTags: NotificationTagConfigUiState) {
        binding.cgNotificationTag.addView(createEventTag(eventTags))
    }

    private fun createEventTag(eventTag: NotificationTagConfigUiState): ActivityTag =
        activityChipOf {
            text = eventTag.eventTag.name
            isChecked = eventTag.isChecked
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.checkTag(eventTag.eventTag.id)
                } else {
                    viewModel.uncheckTag(eventTag.eventTag.id)
                }
            }
        }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(this, ::handleUiEvent)
    }

    private fun handleUiEvent(uiEvent: NotificationTagConfigUiEvent) {
        when (uiEvent) {
            NotificationTagConfigUiEvent.UpdateComplete -> finish()
            NotificationTagConfigUiEvent.UpdateFail -> binding.root.showSnackBar(R.string.notificationtagconfig_interest_tags_update_error_message)
        }
    }

    companion object {
        fun getIntent(context: Context): Intent =
            Intent(context, NotificationTagConfigActivity::class.java)
    }
}
