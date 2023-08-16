package com.emmsale.presentation.ui.setting.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityNotificationConfigBinding
import com.emmsale.presentation.common.extension.showSnackbar
import com.emmsale.presentation.common.views.CancelablePrimaryTag
import com.emmsale.presentation.common.views.ConfirmDialog
import com.emmsale.presentation.common.views.cancelablePrimaryChipOf
import com.emmsale.presentation.ui.setting.notification.uistate.NotificationTagUiState
import com.emmsale.presentation.ui.setting.notification.uistate.NotificationTagsUiState
import com.emmsale.presentation.ui.setting.notificationTagConfig.NotificationTagConfigActivity

class NotificationConfigActivity : AppCompatActivity() {
    private val viewModel: NotificationConfigViewModel by viewModels { NotificationConfigViewModel.factory }
    private val binding: ActivityNotificationConfigBinding by lazy {
        ActivityNotificationConfigBinding.inflate(layoutInflater)
    }
    private val notiTagConfigLauncher = registerForActivityResult(StartActivityForResult()) {
        viewModel.fetchNotificationTags()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        setupObservers()
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
        notiTagConfigLauncher.launch(NotificationTagConfigActivity.getIntent(this))
    }

    private fun setupObservers() {
        setupNotificationTagsObserver()
    }

    private fun setupNotificationTagsObserver() {
        viewModel.notificationTags.observe(this) { uiState ->
            handleNotificationTagsErrors(uiState)
            if (uiState.isTagFetchingSuccess) updateNotificationTagViews(uiState.conferenceTags)
        }
    }

    private fun handleNotificationTagsErrors(uiState: NotificationTagsUiState) {
        when {
            uiState.isTagFetchingError -> showTagFetchingErrorMessage()
            uiState.isTagRemoveError -> showTagRemovingErrorMessage()
        }
    }

    private fun showTagFetchingErrorMessage() {
        binding.root.showSnackbar(R.string.notificationconfig_tag_fetching_error_message)
    }

    private fun showTagRemovingErrorMessage() {
        binding.root.showSnackbar(R.string.notificationconfig_tag_removing_error_message)
    }

    private fun updateNotificationTagViews(conferenceTags: List<NotificationTagUiState>) {
        clearNotificationTagViews()
        addConferenceTags(conferenceTags)
    }

    private fun clearNotificationTagViews() {
        val tagAddButtonCount = 1

        binding.cgNotificationTag.removeViews(
            tagAddButtonCount,
            binding.cgNotificationTag.childCount - tagAddButtonCount,
        )
    }

    private fun addConferenceTags(conferenceTags: List<NotificationTagUiState>) {
        conferenceTags.forEach(::addConferenceTag)
    }

    private fun addConferenceTag(conferenceTag: NotificationTagUiState) {
        binding.cgNotificationTag.addView(createEventTag(conferenceTag))
    }

    private fun createEventTag(eventTag: NotificationTagUiState): CancelablePrimaryTag =
        cancelablePrimaryChipOf {
            text = eventTag.tagName
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
        fun getIntent(context: Context): Intent =
            Intent(context, NotificationConfigActivity::class.java)
    }
}
