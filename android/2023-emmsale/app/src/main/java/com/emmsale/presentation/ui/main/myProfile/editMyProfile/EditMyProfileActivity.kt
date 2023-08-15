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
import com.emmsale.databinding.ActivityEditMyProfileBinding
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.login.LoginActivity
import com.emmsale.presentation.ui.main.myProfile.editMyProfile.recyclerView.ActivitiesAdapter
import com.emmsale.presentation.ui.main.myProfile.editMyProfile.recyclerView.ActivitiesAdapterDecoration
import com.emmsale.presentation.ui.main.myProfile.editMyProfile.recyclerView.FieldsAdapter
import com.emmsale.presentation.ui.main.myProfile.editMyProfile.uiState.EditMyProfileUiState

class EditMyProfileActivity : AppCompatActivity() {

    private val binding: ActivityEditMyProfileBinding by lazy {
        ActivityEditMyProfileBinding.inflate(layoutInflater)
    }

    private val viewModel: EditMyProfileViewModel by viewModels { EditMyProfileViewModel.factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initDataBinding()
        initToolbar()
        initDescriptionEditText()
        initActivitiesRecyclerViews()
        initFieldsRecyclerView()
        setupUiLogic()

        viewModel.fetchMember()
    }

    private fun initDataBinding() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.showFieldTags = ::showFieldTags
    }

    private fun showFieldTags() {
        // TODO("관심 카테고리 다이얼로그 띄우기")
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
        binding.rvEditmyprofileFields.adapter = FieldsAdapter()
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
            title = "활동 삭제",
            message = "해당 활동을 삭제하시겠습니까?",
            positiveButtonLabel = "삭제",
            negativeButtonLabel = "취소",
            onPositiveButtonClick = { viewModel.removeActivity(activityId) },
        ).show()
    }

    private fun setupUiLogic() {
        setupLoginUiLogic()
        setupProfileUiLogic()
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

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, EditMyProfileActivity::class.java))
        }
    }
}
