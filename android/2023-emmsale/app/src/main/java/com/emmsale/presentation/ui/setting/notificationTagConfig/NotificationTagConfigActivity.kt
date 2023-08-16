package com.emmsale.presentation.ui.setting.notificationTagConfig

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityNotificationTagConfigBinding
import com.emmsale.presentation.common.extension.showSnackbar
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.views.ActivityTag
import com.emmsale.presentation.common.views.ConfirmDialog
import com.emmsale.presentation.common.views.activityChipOf
import com.emmsale.presentation.ui.setting.notificationTagConfig.uistate.ConferenceNotificationTagUiState
import com.emmsale.presentation.ui.setting.notificationTagConfig.uistate.NotificationTagsUiState

class NotificationTagConfigActivity : AppCompatActivity() {
    private val viewModel: NotificationTagConfigViewModel by viewModels { NotificationTagConfigViewModel.factory }
    private val binding: ActivityNotificationTagConfigBinding by lazy {
        ActivityNotificationTagConfigBinding.inflate(layoutInflater)
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
        initObservers()
    }

    private fun initClickListener() {
        initToolbarNavigationClickListener()
    }

    private fun initToolbarNavigationClickListener() {
        binding.tbNotificationTagConfig.setNavigationOnClickListener {
            showFinishConfirmDialog()
        }
    }

    private fun showFinishConfirmDialog() {
        ConfirmDialog(
            context = this,
            title = getString(R.string.notificationtagconfig_finish_dialog_title),
            message = getString(R.string.notificationtagconfig_finish_dialog_message),
            onPositiveButtonClick = { finish() },
        ).show()
    }

    private fun initObservers() {
        setupNotificationTagsObserver()
    }

    private fun setupNotificationTagsObserver() {
        viewModel.notificationTags.observe(this) { uiState ->
            handleNotificationTagsErrors(uiState)
            handleNotificationTagsSuccess(uiState)
        }
    }

    private fun handleNotificationTagsErrors(uiState: NotificationTagsUiState) {
        when {
            uiState.isTagFetchingError || uiState.isInterestTagFetchingError -> showTagFetchingErrorMessage()
            uiState.isInterestTagsUpdatingError -> showInterestTagsUpdateErrorMessage()
        }
    }

    private fun showTagFetchingErrorMessage() {
        binding.root.showSnackbar(R.string.notificationtagconfig_tag_fetching_error_message)
    }

    private fun showInterestTagsUpdateErrorMessage() {
        binding.root.showSnackbar(R.string.notificationtagconfig_interest_tags_update_error_message)
    }

    private fun handleNotificationTagsSuccess(uiState: NotificationTagsUiState) {
        when {
            uiState.isTagFetchingSuccess -> updateNotificationTagViews(uiState.conferenceTags)
            uiState.isInterestTagsUpdateSuccess -> finishWithTagUpdateMessage()
        }
    }

    private fun updateNotificationTagViews(conferenceTags: List<ConferenceNotificationTagUiState>) {
        clearNotificationTagViews()
        addConferenceTags(conferenceTags)
    }

    private fun clearNotificationTagViews() {
        binding.cgNotificationTag.removeAllViews()
    }

    private fun addConferenceTags(conferenceTags: List<ConferenceNotificationTagUiState>) {
        conferenceTags.forEach(::addConferenceTag)
    }

    private fun addConferenceTag(conferenceTag: ConferenceNotificationTagUiState) {
        binding.cgNotificationTag.addView(createEventTag(conferenceTag))
    }

    private fun createEventTag(conferenceTag: ConferenceNotificationTagUiState): ActivityTag =
        activityChipOf {
            text = conferenceTag.tagName
            isChecked = conferenceTag.isChecked
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.addInterestTag(conferenceTag.id)
                } else {
                    viewModel.removeInterestTag(conferenceTag.id)
                }
            }
        }

    private fun finishWithTagUpdateMessage() {
        showToast(R.string.notificationtagconfig_interest_tags_update_message)
        finish()
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, NotificationTagConfigActivity::class.java))
        }
    }
}
