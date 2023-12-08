package com.emmsale.presentation.ui.editMyProfile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.emmsale.R
import com.emmsale.databinding.ActivityEditMyProfileBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.extension.dp
import com.emmsale.presentation.common.extension.navigateToApplicationDetailSetting
import com.emmsale.presentation.common.extension.showPermissionRequestDialog
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.imageUtil.getImageFileFromUri
import com.emmsale.presentation.common.imageUtil.isImagePermissionGrantedCompat
import com.emmsale.presentation.common.imageUtil.onImagePermissionCompat
import com.emmsale.presentation.common.recyclerView.IntervalItemDecoration
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.editMyProfile.recyclerView.ActivitiesAdapter
import com.emmsale.presentation.ui.editMyProfile.recyclerView.FieldsAdapter
import com.emmsale.presentation.ui.editMyProfile.uiState.EditMyProfileUiEvent
import com.emmsale.presentation.ui.editMyProfile.uiState.EditMyProfileUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditMyProfileActivity :
    NetworkActivity<ActivityEditMyProfileBinding>(R.layout.activity_edit_my_profile) {

    override val viewModel: EditMyProfileViewModel by viewModels()

    private val fieldsDialog by lazy { FieldsAddBottomDialogFragment() }
    private val educationsDialog by lazy { EducationsAddBottomDialogFragment() }
    private val clubsDialog by lazy { ClubsAddBottomDialogFragment() }

    private val imagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) navigateToGallery()
    }

    private val permissionSettingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (isImagePermissionGrantedCompat()) navigateToGallery()
        }

    private val albumLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val imageUri = it.data?.data ?: return@registerForActivityResult
                val imageFile = getImageFileFromUri(context = this, uri = imageUri)
                viewModel.updateProfileImage(profileImageFile = imageFile)
            }
        }

    private fun navigateToGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        albumLauncher.launch(
            Intent.createChooser(
                intent,
                getString(R.string.all_choose_image_picker_type),
            ),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupDataBinding()
        setupToolbar()
        setupDescriptionEditText()
        setupActivitiesRecyclerViews()
        setupFieldsRecyclerView()

        observeProfile()
        observeUiEvent()
    }

    private fun setupDataBinding() {
        binding.viewModel = viewModel
        binding.onFieldTagsAddButtonClick = ::showFieldTags
        binding.onEducationAddButtonClick = ::showEducations
        binding.onClubAddButtonClick = ::showClubs
        binding.onProfileImageUpdateUiClick = ::startToEditProfileImage
    }

    private fun showFieldTags() {
        if (!fieldsDialog.isAdded) {
            fieldsDialog.show(supportFragmentManager, FieldsAddBottomDialogFragment.TAG)
        }
    }

    private fun showEducations() {
        if (!educationsDialog.isAdded) {
            educationsDialog.show(supportFragmentManager, EducationsAddBottomDialogFragment.TAG)
        }
    }

    private fun showClubs() {
        if (!clubsDialog.isAdded) {
            clubsDialog.show(supportFragmentManager, ClubsAddBottomDialogFragment.TAG)
        }
    }

    private fun startToEditProfileImage() {
        onImagePermissionCompat(
            onGranted = ::navigateToGallery,
            onShouldShowRequestPermissionRationale = { showNavigateToDetailSettingDialog() },
            onDenied = { imagePermissionLauncher.launch(it) },
        )
    }

    private fun showNavigateToDetailSettingDialog() {
        showPermissionRequestDialog(
            message = getString(R.string.all_image_permission_request_dialog_message),
            title = getString(R.string.all_image_permission_request_dialog_title),
            onConfirm = { navigateToApplicationDetailSetting(permissionSettingLauncher) },
            onDenied = {},
        )
    }

    private fun setupToolbar() {
        binding.tbEditmyprofileToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupDescriptionEditText() {
        binding.etEditmyprofileDescription.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.etEditmyprofileDescription.setRawInputType(InputType.TYPE_CLASS_TEXT)
        binding.etEditmyprofileDescription.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.ivEditmyprofileDescriptionPlaceholderIcon.isVisible =
                        s?.length == 0 || s == null

                    if (s.toString().contains('\n')) {
                        binding.etEditmyprofileDescription.setText(
                            s.toString().filterNot { it == '\n' },
                        )
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            },
        )

        binding.etEditmyprofileDescription.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val description = binding.etEditmyprofileDescription.text.toString()
                viewModel.updateDescription(description)
                binding.etEditmyprofileDescription.clearFocus()
            }
            false
        }
    }

    private fun setupActivitiesRecyclerViews() {
        val decoration = IntervalItemDecoration(height = 2.dp)
        listOf(
            binding.rvEditmyprofileClubs,
            binding.rvEditmyprofileEducations,
        ).forEach {
            it.apply {
                adapter = ActivitiesAdapter(::showActivityRemoveConfirmDialog)
                itemAnimator = null
                addItemDecoration(decoration)
            }
        }
    }

    private fun showActivityRemoveConfirmDialog(activityId: Long) {
        WarningDialog(
            context = this,
            title = getString(R.string.editmyprofile_activity_remove_warning_title),
            message = getString(R.string.editmyprofile_activity_remove_warning_message),
            positiveButtonLabel = getString(R.string.all_delete_button_label),
            negativeButtonLabel = getString(R.string.all_cancel),
            onPositiveButtonClick = { viewModel.removeActivity(activityId) },
        ).show()
    }

    private fun setupFieldsRecyclerView() {
        binding.rvEditmyprofileFields.adapter = FieldsAdapter(::removeField)
    }

    private fun removeField(activityId: Long) {
        viewModel.removeActivity(activityId)
    }

    private fun observeProfile() {
        viewModel.profile.observe(this, ::handleActivities)
    }

    private fun handleActivities(profile: EditMyProfileUiState) {
        (binding.rvEditmyprofileFields.adapter as FieldsAdapter).submitList(profile.member.fields)
        (binding.rvEditmyprofileClubs.adapter as ActivitiesAdapter).submitList(profile.member.clubs)
        (binding.rvEditmyprofileEducations.adapter as ActivitiesAdapter).submitList(profile.member.educations)
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(this, ::handleUiEvent)
    }

    private fun handleUiEvent(uiEvent: EditMyProfileUiEvent) {
        when (uiEvent) {
            EditMyProfileUiEvent.ActivitiesAddFail -> binding.root.showSnackBar(getString(R.string.editmyprofile_acitivities_add_error_message))
            EditMyProfileUiEvent.ActivityRemoveFail -> binding.root.showSnackBar(getString(R.string.editmyprofile_activity_remove_error_message))
            EditMyProfileUiEvent.DescriptionUpdateFail -> binding.root.showSnackBar(getString(R.string.editmyprofile_update_description_error_message))
            EditMyProfileUiEvent.ProfileImageUpdateFail -> binding.root.showSnackBar(getString(R.string.editmyprofile_update_profile_image_error_message))
        }
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, EditMyProfileActivity::class.java))
        }
    }
}
