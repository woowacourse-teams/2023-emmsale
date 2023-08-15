package com.emmsale.presentation.ui.main.myProfile.editMyProfile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.emmsale.R
import com.emmsale.databinding.ActivityEditMyProfileBinding
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.login.LoginActivity
import com.emmsale.presentation.ui.main.myProfile.editMyProfile.recyclerView.ActivitiesAdapter
import com.emmsale.presentation.ui.main.myProfile.editMyProfile.recyclerView.ActivitiesAdapterDecoration
import com.emmsale.presentation.ui.main.myProfile.editMyProfile.recyclerView.FieldsAdapter
import com.emmsale.presentation.ui.main.myProfile.editMyProfile.uiState.EditMyProfileErrorEvent
import com.emmsale.presentation.ui.main.myProfile.editMyProfile.uiState.EditMyProfileUiState
import kotlinx.coroutines.launch

class EditMyProfileActivity : AppCompatActivity() {

    private val binding: ActivityEditMyProfileBinding by lazy {
        ActivityEditMyProfileBinding.inflate(layoutInflater)
    }

    private val viewModel: EditMyProfileViewModel by viewModels { EditMyProfileViewModel.factory }

    private val fieldsDialog by lazy { FieldsAddBottomDialogFragment() }
    private val educationsDialog by lazy { EducationsAddBottomDialogFragment() }
    private val clubsDialog by lazy { ClubsAddBottomDialogFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initDataBinding()
        initToolbar()
        initDescriptionEditText()
        initActivitiesRecyclerViews()
        initFieldsRecyclerView()
        setupUiLogic()

        lifecycleScope.launch {
            viewModel.fetchMember().join()
            viewModel.fetchAllActivities()
        }
    }

    private fun initDataBinding() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.showFieldTags = ::showFieldTags
        binding.showEducations = ::showEducations
        binding.showClubs = ::showClubs
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

    private fun handleErrors(errorEvents: List<EditMyProfileErrorEvent>) {
        errorEvents.forEach {
            when (it) {
                EditMyProfileErrorEvent.MEMBER_FETCHING -> {}
                EditMyProfileErrorEvent.DESCRIPTION_UPDATE -> showToast(getString(R.string.editmyprofile_update_description_error_message))
                EditMyProfileErrorEvent.ACTIVITIES_FETCHING -> {}
            }
        }
        viewModel.errorEvents.clear()
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, EditMyProfileActivity::class.java))
        }
    }
}
