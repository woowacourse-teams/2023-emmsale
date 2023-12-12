package com.emmsale.presentation.ui.feedWriting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.emmsale.R
import com.emmsale.databinding.ActivityFeedWritingBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.extension.dp
import com.emmsale.presentation.common.extension.navigateToApplicationDetailSetting
import com.emmsale.presentation.common.extension.showPermissionRequestDialog
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.imageUtil.getImageFileFromUri
import com.emmsale.presentation.common.imageUtil.isImagePermissionGrantedCompat
import com.emmsale.presentation.common.imageUtil.onImagePermissionCompat
import com.emmsale.presentation.common.recyclerView.IntervalItemDecoration
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.feedDetail.FeedDetailActivity
import com.emmsale.presentation.ui.feedWriting.FeedWritingViewModel.Companion.EVENT_ID_KEY
import com.emmsale.presentation.ui.feedWriting.recyclerView.FeedWritingImageAdapter
import com.emmsale.presentation.ui.feedWriting.uiState.FeedWritingUiEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedWritingActivity :
    NetworkActivity<ActivityFeedWritingBinding>(R.layout.activity_feed_writing) {

    override val viewModel: FeedWritingViewModel by viewModels()

    private val imagesAdapter: FeedWritingImageAdapter by lazy {
        FeedWritingImageAdapter(deleteImage = viewModel::deleteImageUri)
    }

    private val imagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) showAlbum()
    }

    private val requestImagePermissionSettingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (isImagePermissionGrantedCompat()) showAlbum()
        }

    private val albumLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val imageUris = getImageUriPaths(result.data ?: return@registerForActivityResult)
            when {
                imageUris.size > MAX_IMAGE_COUNT -> {
                    showAlbum()
                    showToast(R.string.post_writing_max_image_count_warning_message)
                }

                imageUris.isEmpty() -> Unit

                else -> viewModel.setImageUris(imageUris = imageUris)
            }
        }

    private fun getImageUriPaths(intent: Intent): List<String> {
        val clipData = intent.clipData ?: return listOf(intent.data.toString())
        return (0 until clipData.itemCount).mapNotNull { clipData.getItemAt(it).uri.toString() }
    }

    private fun showAlbum() {
        onImagePermissionCompat(
            onGranted = ::navigateToAlbum,
            onShouldShowRequestPermissionRationale = { showImagePermissionRequestDialog() },
            onDenied = { imagePermissionLauncher.launch(it) },
        )
    }

    private fun navigateToAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        albumLauncher.launch(
            Intent.createChooser(
                intent,
                getString(R.string.all_choose_image_picker_type),
            ),
        )
    }

    private fun showImagePermissionRequestDialog() {
        showPermissionRequestDialog(
            message = getString(R.string.all_image_permission_request_dialog_message),
            title = getString(R.string.all_image_permission_request_dialog_title),
            onConfirm = { navigateToApplicationDetailSetting(requestImagePermissionSettingLauncher) },
            onDenied = {},
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDataBinding()
        setupBackPressedDispatcher()
        setupToolbar()
        setupImagesAdapter()

        observeCanSubmit()
        observeImageUrls()
        observeUiEvent()
    }

    private fun setupDataBinding() {
        setContentView(binding.root)
        binding.vm = viewModel
        binding.showAlbum = ::showAlbum
    }

    private fun setupBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewModel.isChanged()) showFinishConfirmDialog() else finish()
                }
            },
        )
    }

    private fun showFinishConfirmDialog() {
        WarningDialog(
            context = this,
            title = getString(R.string.recruitmentpostwriting_writing_cancel_confirm_dialog_title),
            message = getString(R.string.recruitmentpostwriting_writing_cancel_confirm_dialog_message),
            positiveButtonLabel = getString(R.string.all_okay),
            negativeButtonLabel = getString(R.string.all_cancel),
            onPositiveButtonClick = { finish() },
        ).show()
    }

    private fun setupToolbar() {
        binding.tbToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.tbToolbar.setOnMenuItemClickListener {
            val imageUris = viewModel.imageUris.value
            val imageFiles = imageUris.map { getImageFileFromUri(this, Uri.parse(it)) }
            viewModel.uploadPost(imageFiles)
            true
        }
    }

    private fun setupImagesAdapter() {
        binding.rvFeedWritingImageList.adapter = imagesAdapter
        binding.rvFeedWritingImageList.addItemDecoration(IntervalItemDecoration(width = 10.dp))
    }

    private fun observeCanSubmit() {
        viewModel.calSubmit.observe(this) { canSubmit ->
            binding.tbToolbar.menu.findItem(R.id.register).isEnabled = canSubmit
        }
    }

    private fun observeImageUrls() {
        viewModel.imageUris.observe(this) { imageUrls ->
            imagesAdapter.submitList(imageUrls)
        }
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(this, ::handleUiEvent)
    }

    private fun handleUiEvent(uiEvent: FeedWritingUiEvent) {
        when (uiEvent) {
            is FeedWritingUiEvent.PostComplete -> {
                FeedDetailActivity.startActivity(this, uiEvent.feedId)
                finish()
            }

            FeedWritingUiEvent.PostFail -> binding.root.showSnackBar(R.string.post_writing_upload_error)
        }
    }

    companion object {
        private const val MAX_IMAGE_COUNT = 5

        fun startActivity(context: Context, eventId: Long) {
            val intent = Intent(context, FeedWritingActivity::class.java)
                .putExtra(EVENT_ID_KEY, eventId)
            context.startActivity(intent)
        }
    }
}
