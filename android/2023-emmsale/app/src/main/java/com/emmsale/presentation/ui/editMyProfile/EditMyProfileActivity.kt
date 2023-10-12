package com.emmsale.presentation.ui.editMyProfile

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.emmsale.R
import com.emmsale.databinding.ActivityEditMyProfileBinding
import com.emmsale.presentation.common.extension.navigateToApplicationDetailSetting
import com.emmsale.presentation.common.extension.showPermissionRequestDialog
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.imageUtil.convertToAbsolutePath
import com.emmsale.presentation.common.imageUtil.isPhotoPickerAvailable
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.editMyProfile.recyclerView.ActivitiesAdapter
import com.emmsale.presentation.ui.editMyProfile.recyclerView.ActivitiesAdapterDecoration
import com.emmsale.presentation.ui.editMyProfile.recyclerView.FieldsAdapter
import com.emmsale.presentation.ui.editMyProfile.uiState.EditMyProfileErrorEvent
import com.emmsale.presentation.ui.editMyProfile.uiState.EditMyProfileUiState
import com.emmsale.presentation.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditMyProfileActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEditMyProfileBinding.inflate(layoutInflater) }
    private val viewModel: EditMyProfileViewModel by viewModels()

    private val fieldsDialog by lazy { FieldsAddBottomDialogFragment() }
    private val educationsDialog by lazy { EducationsAddBottomDialogFragment() }
    private val clubsDialog by lazy { ClubsAddBottomDialogFragment() }

    private val requestImagePermissionDialogLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) showAlbum()
    }

    private val requestImagePermissionSettingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (isImageAccessPermissionGranted()) showAlbum()
        }

    private val underTiramisuAlbumLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val imageUrl = getImageUrlFromActivityResult(result) ?: return@registerForActivityResult
            viewModel.updateProfileImage(profileImageUrl = imageUrl)
        }

    private val overTiramisuAlbumLauncher = registerForActivityResult(
        PickVisualMedia(),
    ) { uri ->
        if (uri == null) return@registerForActivityResult
        viewModel.updateProfileImage(
            uri.convertToAbsolutePath(this) ?: return@registerForActivityResult,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initDataBinding()
        initToolbar()
        initDescriptionEditText()
        initActivitiesRecyclerViews()
        initFieldsRecyclerView()
        setupUiLogic()
    }

    private fun initDataBinding() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.showFieldTags = ::showFieldTags
        binding.showEducations = ::showEducations
        binding.showClubs = ::showClubs
        binding.editProfileImage = ::showAlbum
    }

    private fun getImageUrlFromActivityResult(result: ActivityResult): String? {
        val clipData = result.data?.clipData

        val uri = clipData?.getItemAt(0)?.uri ?: return null
        return uri.convertToAbsolutePath(this)
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

    private fun initToolbar() {
        binding.tbEditmyprofileToolbar.setNavigationOnClickListener { finish() }
    }

    private fun initDescriptionEditText() {
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

    private fun initFieldsRecyclerView() {
        binding.rvEditmyprofileFields.adapter = FieldsAdapter(::removeField)
    }

    private fun removeField(activityId: Long) {
        viewModel.removeActivity(activityId)
    }

    private fun initActivitiesRecyclerViews() {
        val decoration = ActivitiesAdapterDecoration()
        listOf(
            binding.rvEditmyprofileClubs,
            binding.rvEditmyprofileEducations,
        ).forEach {
            it.apply {
                adapter = ActivitiesAdapter(::removeActivity)
                itemAnimator = null
                addItemDecoration(decoration)
            }
        }
    }

    private fun removeActivity(activityId: Long) {
        WarningDialog(
            context = this,
            title = getString(R.string.editmyprofile_activity_remove_warning_title),
            message = getString(R.string.editmyprofile_activity_remove_warning_message),
            positiveButtonLabel = getString(R.string.all_delete_button_label),
            negativeButtonLabel = getString(R.string.all_cancel),
            onPositiveButtonClick = { viewModel.removeActivity(activityId) },
        ).show()
    }

    private fun setupUiLogic() {
        setupLoginUiLogic()
        setupProfileUiLogic()
        setupErrorsUiLogic()
    }

    private fun setupLoginUiLogic() {
        viewModel.isLogin.observe(this) {
            handleNotLogin(it)
        }
    }

    private fun handleNotLogin(isLogin: Boolean) {
        if (!isLogin) {
            LoginActivity.startActivity(this)
            finish()
        }
    }

    private fun setupProfileUiLogic() {
        viewModel.profile.observe(this) {
            handleFields(it)
            handleActivities(it)
        }
    }

    private fun handleFields(profile: EditMyProfileUiState) {
        (binding.rvEditmyprofileFields.adapter as FieldsAdapter).submitList(profile.fields)
    }

    private fun handleActivities(profile: EditMyProfileUiState) {
        (binding.rvEditmyprofileClubs.adapter as ActivitiesAdapter).submitList(profile.clubs)
        (binding.rvEditmyprofileEducations.adapter as ActivitiesAdapter).submitList(profile.educations)
    }

    private fun setupErrorsUiLogic() {
        viewModel.errorEvents.observe(this) {
            handleErrors(it)
        }
    }

    private fun handleErrors(errorEvent: EditMyProfileErrorEvent?) {
        when (errorEvent) {
            EditMyProfileErrorEvent.DESCRIPTION_UPDATE -> binding.root.showSnackBar(getString(R.string.editmyprofile_update_description_error_message))
            EditMyProfileErrorEvent.ACTIVITY_REMOVE -> binding.root.showSnackBar(getString(R.string.editmyprofile_activity_remove_error_message))
            EditMyProfileErrorEvent.ACTIVITIES_ADD -> binding.root.showSnackBar(getString(R.string.editmyprofile_acitivities_add_error_message))
            EditMyProfileErrorEvent.PROFILE_IMAGE_UPDATE -> binding.root.showSnackBar(getString(R.string.editmyprofile_update_profile_image_error_message))
            else -> return
        }
        viewModel.removeError()
    }

    private fun showAlbum() {
        if (isPhotoPickerAvailable()) {
            overTiramisuAlbumLauncher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        } else {
            if (!isImageAccessPermissionGranted()) {
                if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                    showImagePermissionRequestDialog()
                } else {
                    requestImagePermissionDialogLauncher.launch(READ_EXTERNAL_STORAGE)
                }
            } else {
                val intent = Intent(Intent.ACTION_PICK).apply {
                    type = "image/*"
                }
                underTiramisuAlbumLauncher.launch(intent)
            }
        }
    }

    private fun showImagePermissionRequestDialog() {
        showPermissionRequestDialog(
            message = getString(R.string.all_image_permission_request_dialog_message),
            title = getString(R.string.all_image_permission_request_dialog_title),
            onConfirm = { navigateToApplicationDetailSetting(requestImagePermissionSettingLauncher) },
            onDenied = {},
        )
    }

    private fun isImageAccessPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            READ_EXTERNAL_STORAGE,
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, EditMyProfileActivity::class.java))
        }
    }
}
