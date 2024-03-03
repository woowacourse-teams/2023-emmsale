package com.emmsale.presentation.ui.notificationConfig

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.emmsale.R
import com.emmsale.model.EventTag
import com.emmsale.databinding.ActivityNotificationConfigBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.extension.checkPostNotificationPermission
import com.emmsale.presentation.common.extension.navigateToNotificationSettings
import com.emmsale.presentation.common.extension.showPermissionRequestDialog
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegate
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegateImpl
import com.emmsale.presentation.common.views.CancelablePrimaryTag
import com.emmsale.presentation.common.views.ConfirmDialog
import com.emmsale.presentation.common.views.cancelablePrimaryChipOf
import com.emmsale.presentation.ui.notificationConfig.uiState.NotificationConfigUiEvent
import com.emmsale.presentation.ui.notificationTagConfig.NotificationTagConfigActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationConfigActivity :
    NetworkActivity<ActivityNotificationConfigBinding>(R.layout.activity_notification_config),
    FirebaseAnalyticsDelegate by FirebaseAnalyticsDelegateImpl("notification_config") {

    override val viewModel: NotificationConfigViewModel by viewModels()

    private val notiTagConfigLauncher = registerForActivityResult(StartActivityForResult()) {
        viewModel.fetchNotificationTags()
    }
    private val settingLauncher = registerForActivityResult(StartActivityForResult()) {
        viewModel.setAllNotificationReceiveConfig(checkPostNotificationPermission())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        registerScreen(this)

        setupDateBinding()
        setupToolbar()

        observeConfig()
        observeNotificationTags()
        observeUiEvent()
    }

    private fun setupDateBinding() {
        binding.viewModel = viewModel
        binding.onTagAddButtonClick =
            { notiTagConfigLauncher.launch(NotificationTagConfigActivity.getIntent(this)) }

        binding.onAllNotificationReceiveConfigSwitchClick = { isChecked ->
            if (!checkPostNotificationPermission() && isChecked) {
                showPermissionRequestDialog()
            } else {
                viewModel.setAllNotificationReceiveConfig(isChecked)
            }
        }

        binding.onInterestTagConfigSwitchClick =
            { viewModel.setInterestEventNotificationReceiveConfig(it) }

        binding.onCommentConfigSwitchClick = { viewModel.setCommentNotificationReceiveConfig(it) }
        binding.onMessageConfigSwitchClick = { viewModel.setMessageNotificationReceiveConfig(it) }
    }

    private fun showPermissionRequestDialog() {
        showPermissionRequestDialog(
            onConfirm = { navigateToNotificationSettings(settingLauncher) },
            onDenied = { binding.switchAllNotificationReceiveConfig.isChecked = false },
        )
    }

    private fun setupToolbar() {
        binding.tbNotificationConfig.setOnMenuItemClickListener {
            if (it.itemId == R.id.close) finish()
            true
        }
    }

    private fun observeConfig() {
        viewModel.notificationConfig.observe(this) { config ->
            val isNotificationPermissionChecked = checkPostNotificationPermission()
            val isNotificationReceive = config.isNotificationReceive

            binding.switchAllNotificationReceiveConfig.isChecked =
                isNotificationPermissionChecked && isNotificationReceive

            binding.clNotificationChannelSetting.isVisible =
                isNotificationPermissionChecked && isNotificationReceive

            binding.switchCommentNotificationReceiveConfig.isChecked =
                config.isCommentNotificationReceive

            binding.switchInterestTagNotificationReceiveConfig.isChecked =
                config.isInterestEventNotificationReceive

            binding.switchMessageNotificationReceiveConfig.isChecked =
                config.isMessageNotificationReceive
        }
    }

    private fun observeNotificationTags() {
        viewModel.notificationTags.observe(this, ::updateNotificationTagViews)
    }

    private fun updateNotificationTagViews(conferenceTags: List<EventTag>) {
        clearNotificationTagViews()
        addEventTags(conferenceTags)
    }

    private fun clearNotificationTagViews() {
        val tagAddButtonCount = 1

        binding.cgNotificationTag.removeViews(
            tagAddButtonCount,
            binding.cgNotificationTag.childCount - tagAddButtonCount,
        )
    }

    private fun addEventTags(eventTags: List<EventTag>) {
        eventTags.forEach(::addEventTag)
    }

    private fun addEventTag(eventTag: EventTag) {
        binding.cgNotificationTag.addView(createEventTag(eventTag))
    }

    private fun createEventTag(eventTag: EventTag): CancelablePrimaryTag =
        cancelablePrimaryChipOf {
            text = eventTag.name
            setOnClickListener { showTagRemoveConfirmDialog(eventTag.id) }
        }

    private fun showTagRemoveConfirmDialog(tagId: Long) {
        ConfirmDialog(
            context = this,
            title = getString(R.string.notificationconfig_tag_remove_confirm_dialog_title),
            message = getString(R.string.notificationconfig_tag_remove_confirm_dialog_message),
            onPositiveButtonClick = { viewModel.removeNotificationTag(tagId) },
        ).show()
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(this, ::handleUiEvent)
    }

    private fun handleUiEvent(uiEvent: NotificationConfigUiEvent) {
        when (uiEvent) {
            NotificationConfigUiEvent.InterestTagRemoveFail -> binding.root.showSnackBar(R.string.notificationconfig_tag_removing_error_message)
        }
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, NotificationConfigActivity::class.java))
        }
    }
}
