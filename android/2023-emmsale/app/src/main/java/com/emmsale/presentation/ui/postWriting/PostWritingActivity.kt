package com.emmsale.presentation.ui.postWriting

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.emmsale.R
import com.emmsale.databinding.ActivityPostWritingBinding
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.extension.navigateToApplicationDetailSetting
import com.emmsale.presentation.common.extension.showPermissionRequestDialog
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.imageUtil.convertToAbsolutePath
import com.emmsale.presentation.common.imageUtil.isPhotoPickerAvailable
import com.emmsale.presentation.ui.feedDetail.FeedDetailActivity
import com.emmsale.presentation.ui.postWriting.PostWritingViewModel.Companion.EVENT_ID_KEY
import com.emmsale.presentation.ui.postWriting.recyclerView.PostWritingImageAdapter
import com.emmsale.presentation.ui.postWriting.uiState.PostUploadResultUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostWritingActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPostWritingBinding.inflate(layoutInflater) }
    private val viewModel: PostWritingViewModel by viewModels()

    private val adapter: PostWritingImageAdapter by lazy {
        PostWritingImageAdapter(deleteImage = viewModel::deleteImageUrl)
    }

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
            val imageUrls = getImageUrlsFromActivityResult(result)
            when {
                imageUrls.size > MAX_IMAGE_COUNT -> {
                    showAlbum()
                    showToast(R.string.post_writing_max_image_count_warning_message)
                }

                imageUrls.isEmpty() -> Unit
                else -> viewModel.fetchImageUrls(imageUrls = imageUrls)
            }
        }

    private val overTiramisuAlbumLauncher = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(MAX_IMAGE_COUNT),
    ) { uris ->
        viewModel.fetchImageUrls(uris.map { it.convertToAbsolutePath(this) ?: "" })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpBinding()
        setUpPostUploadResult()
        setUpImageUrls()
        setUpRegisterButtonClick()
        setUpBackButtonClick()
    }

    private fun getImageUrlsFromActivityResult(result: ActivityResult): List<String> {
        val clipData = result.data?.clipData

        if (clipData != null) {
            val count = clipData.itemCount
            return (0 until count).mapNotNull { i ->
                val uri = clipData.getItemAt(i).uri
                uri.convertToAbsolutePath(this)
            }
        }
        return emptyList()
    }

    private fun setUpBinding() {
        setContentView(binding.root)
        binding.rvPostWritingImageList.adapter = adapter
        binding.vm = viewModel
        binding.showAlbum = ::showAlbum
        binding.lifecycleOwner = this
    }

    private fun setUpPostUploadResult() {
        viewModel.postUploadResult.observe(this, ::handleUploadPostResult)
    }

    private fun handleUploadPostResult(event: Event<PostUploadResultUiState>) {
        val content = event.getContentIfNotHandled() ?: return
        when (content.fetchResult) {
            FetchResult.SUCCESS -> {
                FeedDetailActivity.startActivity(this, content.responseId!!)
                finish()
            }

            FetchResult.ERROR -> showToast(getString(R.string.post_writing_upload_error))

            FetchResult.LOADING -> Unit
        }
    }

    private fun setUpImageUrls() {
        viewModel.imageUrls.observe(this) { imageUrls ->
            adapter.submitList(imageUrls)
        }
    }

    private fun setUpRegisterButtonClick() {
        binding.tbToolbar.setOnMenuItemClickListener {
            uploadPost()
            true
        }
    }

    private fun uploadPost() {
        when {
            !viewModel.isTitleValid() -> showToast(getString(R.string.post_writing_title_warning))
            !viewModel.isContentValid() -> showToast(getString(R.string.post_writing_content_warning))
            else -> viewModel.uploadPost()
        }
    }

    private fun showAlbum() {
        if (isPhotoPickerAvailable()) {
            overTiramisuAlbumLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }
                underTiramisuAlbumLauncher.launch(intent)
            }
        }
    }

    private fun isImageAccessPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            READ_EXTERNAL_STORAGE,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showImagePermissionRequestDialog() {
        showPermissionRequestDialog(
            message = getString(R.string.all_image_permission_request_dialog_message),
            title = getString(R.string.all_image_permission_request_dialog_title),
            onConfirm = { navigateToApplicationDetailSetting(requestImagePermissionSettingLauncher) },
            onDenied = {},
        )
    }

    private fun setUpBackButtonClick() {
        binding.tbToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    companion object {
        private const val MAX_IMAGE_COUNT = 5

        fun startActivity(context: Context, eventId: Long) {
            val intent = Intent(context, PostWritingActivity::class.java)
                .putExtra(EVENT_ID_KEY, eventId)
            context.startActivity(intent)
        }
    }
}
