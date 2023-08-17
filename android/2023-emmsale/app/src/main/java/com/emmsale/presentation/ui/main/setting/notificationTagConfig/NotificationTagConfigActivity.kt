package com.emmsale.presentation.ui.main.setting.notificationTagConfig

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityNotificationTagConfigBinding
import com.emmsale.presentation.common.extension.showSnackbar
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegate
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegateImpl
import com.emmsale.presentation.common.views.ActivityTag
import com.emmsale.presentation.common.views.ConfirmDialog
import com.emmsale.presentation.common.views.activityChipOf
import com.emmsale.presentation.ui.main.setting.notificationTagConfig.uistate.NotificationTagConfigUiState
import com.emmsale.presentation.ui.main.setting.notificationTagConfig.uistate.NotificationTagsConfigUiState

class NotificationTagConfigActivity :
    AppCompatActivity(),
    FirebaseAnalyticsDelegate by FirebaseAnalyticsDelegateImpl("notification_tag") {
    private val viewModel: NotificationTagConfigViewModel by viewModels { NotificationTagConfigViewModel.factory }
    private val binding: ActivityNotificationTagConfigBinding by lazy {
        ActivityNotificationTagConfigBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerScreen(this)
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

    private fun handleNotificationTagsErrors(uiState: NotificationTagsConfigUiState) {
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

    private fun handleNotificationTagsSuccess(uiState: NotificationTagsConfigUiState) {
        when {
            uiState.isTagFetchingSuccess -> updateNotificationTagViews(uiState.conferenceTags)
            uiState.isInterestTagsUpdateSuccess -> finishWithTagUpdateMessage()
        }
    }

    private fun updateNotificationTagViews(conferenceTags: List<NotificationTagConfigUiState>) {
        clearNotificationTagViews()
        addConferenceTags(conferenceTags)
    }

    private fun clearNotificationTagViews() {
        binding.cgNotificationTag.removeAllViews()
    }

    private fun addConferenceTags(conferenceTags: List<NotificationTagConfigUiState>) {
        conferenceTags.forEach(::addConferenceTag)
    }

    private fun addConferenceTag(conferenceTag: NotificationTagConfigUiState) {
        binding.cgNotificationTag.addView(createEventTag(conferenceTag))
    }

    private fun createEventTag(eventTag: NotificationTagConfigUiState): ActivityTag =
        activityChipOf {
            text = eventTag.tagName
            isChecked = eventTag.isChecked
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.addInterestTag(eventTag.id)
                } else {
                    viewModel.removeInterestTag(eventTag.id)
                }
            }
        }

    private fun finishWithTagUpdateMessage() {
        showToast(R.string.notificationtagconfig_interest_tags_update_message)
        finish()
    }

    companion object {
        fun getIntent(context: Context): Intent =
            Intent(context, NotificationTagConfigActivity::class.java)
    }
}
