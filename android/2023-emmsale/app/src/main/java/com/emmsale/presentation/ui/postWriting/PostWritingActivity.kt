package com.emmsale.presentation.ui.postWriting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityPostWritingBinding
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.extension.navigateToApplicationDetailSetting
import com.emmsale.presentation.common.extension.showPermissionRequestDialog
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.imageUtil.getImageFileFromUri
import com.emmsale.presentation.common.imageUtil.isImagePermissionGrantedCompat
import com.emmsale.presentation.common.imageUtil.onImagePermissionCompat
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

                else -> viewModel.fetchImageUris(imageUrls = imageUris)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpBinding()
        setUpPostUploadResult()
        setUpImageUrls()
        setUpRegisterButtonClick()
        setUpBackButtonClick()
    }

    private fun getImageUriPaths(intent: Intent): List<String> {
        val clipData = intent.clipData ?: return listOf(intent.data.toString())
        return (0 until clipData.itemCount).mapNotNull { clipData.getItemAt(it).uri.toString() }
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
        viewModel.imageUris.observe(this) { imageUrls ->
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

            else -> {
                val imageUris = viewModel.imageUris.value
                val imageFiles = imageUris.map { getImageFileFromUri(this, Uri.parse(it)) }
                viewModel.uploadPost(imageFiles)
            }
        }
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
