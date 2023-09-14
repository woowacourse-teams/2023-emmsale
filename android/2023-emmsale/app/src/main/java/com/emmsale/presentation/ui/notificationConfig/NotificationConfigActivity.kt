package com.emmsale.presentation.ui.notificationConfig

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.emmsale.R
import com.emmsale.data.model.EventTag
import com.emmsale.databinding.ActivityNotificationConfigBinding
import com.emmsale.presentation.common.Event
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
import com.emmsale.presentation.ui.notificationConfig.uiState.NotificationTagsUiState
import com.emmsale.presentation.ui.notificationTagConfig.NotificationTagConfigActivity

class NotificationConfigActivity :
    AppCompatActivity(),
    FirebaseAnalyticsDelegate by FirebaseAnalyticsDelegateImpl("notification_config") {
    private val viewModel: NotificationConfigViewModel by viewModels { NotificationConfigViewModel.factory }
    private val binding: ActivityNotificationConfigBinding by lazy {
        ActivityNotificationConfigBinding.inflate(layoutInflater)
    }
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
        initView()
        setupObservers()
    }

    private fun initView() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        initClickListener()
        initNotificationConfigSwitch()
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
        notiTagConfigLauncher.launch(NotificationTagConfigActivity.getIntent(this))
    }

    private fun initNotificationConfigSwitch() {
        binding.switchAllNotificationReceiveConfig.setOnCheckedChangeListener { _, isChecked ->
            if (!checkPostNotificationPermission() && isChecked) {
                showPermissionRequestDialog()
                return@setOnCheckedChangeListener
            }
            viewModel.setAllNotificationReceiveConfig(isChecked)
        }
        binding.switchFollowNotificationReceiveConfig.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setFollowNotificationReceiveConfig(isChecked)
        }
        binding.switchCommentNotificationReceiveConfig.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setCommentNotificationReceiveConfig(isChecked)
        }
        binding.switchInterestTagNotificationReceiveConfig.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setInterestEventNotificationReceiveConfig(isChecked)
        }
    }

    private fun showPermissionRequestDialog() {
        showPermissionRequestDialog(
            onConfirm = { navigateToNotificationSettings(settingLauncher) },
            onDenied = { binding.switchAllNotificationReceiveConfig.isChecked = false },
        )
    }

    private fun setupObservers() {
        setupConfigObserver()
        setupNotificationTagsObserver()
        setupNotificationConfigUiEventObserver()
    }

    private fun setupConfigObserver() {
        viewModel.notificationConfig.observe(this) { config ->
            val isNotificationPermissionChecked = checkPostNotificationPermission()
            val isNotificationReceive = config.isNotificationReceive

            binding.switchAllNotificationReceiveConfig.isChecked =
                isNotificationPermissionChecked && isNotificationReceive

            binding.clNotificationChannelSetting.isVisible =
                isNotificationPermissionChecked && isNotificationReceive

            binding.switchFollowNotificationReceiveConfig.isChecked =
                config.isFollowNotificationReceive

            binding.switchCommentNotificationReceiveConfig.isChecked =
                config.isCommentNotificationReceive

            binding.switchInterestTagNotificationReceiveConfig.isChecked =
                config.isInterestEventNotificationReceive
        }
    }

    private fun setupNotificationTagsObserver() {
        viewModel.notificationTags.observe(this) { uiState ->
            if (uiState !is NotificationTagsUiState.Success) return@observe
            updateNotificationTagViews(uiState.tags)
        }
    }

    private fun setupNotificationConfigUiEventObserver() {
        viewModel.uiEvent.observe(this) { event ->
            handleNotificationTagsErrors(event)
        }
    }

    private fun handleNotificationTagsErrors(event: Event<NotificationConfigUiEvent>) {
        val content = event.getContentIfNotHandled() ?: return
        when (content) {
            NotificationConfigUiEvent.INTEREST_TAG_REMOVE_ERROR -> showTagRemovingErrorMessage()
            NotificationConfigUiEvent.NONE -> {}
        }
    }

    private fun showTagRemovingErrorMessage() {
        binding.root.showSnackBar(R.string.notificationconfig_tag_removing_error_message)
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
            onPositiveButtonClick = { viewModel.removeInterestTagById(tagId) },
        ).show()
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, NotificationConfigActivity::class.java))
        }
    }
}
