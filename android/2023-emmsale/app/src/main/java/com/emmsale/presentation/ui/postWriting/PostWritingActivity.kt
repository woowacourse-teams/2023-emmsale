package com.emmsale.presentation.ui.postWriting

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.emmsale.presentation.common.extension.showToast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpBinding()
        setUpPostUploadResult()
        setUpImageUrls()
    }

    // use when build version under 33
    private val lowVersionAlbumLauncher =
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

    private fun getImageUrlsFromActivityResult(result: ActivityResult): List<String> {
        val clipData = result.data?.clipData

        if (clipData != null) {
            val count = clipData.itemCount
            return (0 until count).mapNotNull { i ->
                val uri = clipData.getItemAt(i).uri
                getAbsolutePathFromUri(uri)
            }
        }
        return emptyList()
    }

    // use when build version 33
    private val highVersionAlbumLauncher = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(MAX_IMAGE_COUNT),
    ) { uris ->
        viewModel.fetchImageUrls(uris.map { getAbsolutePathFromUri(it) ?: "" })
    }

    private fun getAbsolutePathFromUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
        return cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            it.getString(columnIndex)
        }
    }

    private fun setUpBinding() {
        setContentView(binding.root)
        binding.rvPostWritingImageList.adapter = adapter
        binding.vm = viewModel
        binding.navigateToBack = ::navigateToBack
        binding.showAlbum = ::showAlbum
        binding.uploadPost = ::uploadPost
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

            FetchResult.ERROR -> {
                showToast(getString(R.string.post_writing_upload_error))
            }

            FetchResult.LOADING -> Unit
        }
    }

    private fun setUpImageUrls() {
        viewModel.imageUrls.observe(this) { imageUrls ->
            adapter.submitList(imageUrls)
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
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            highVersionAlbumLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            when {
                isImageAccessPermissionGranted() -> {
                    val intent = Intent().apply {
                        type = "image/*"
                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                        action = Intent.ACTION_PICK
                    }
                    lowVersionAlbumLauncher.launch(intent)
                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_STORAGE,
                    )
                }

                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_STORAGE,
                    )
                }
            }
        }
    }

    private fun isImageAccessPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showAlbum()
                } else {
                    showToast(getString(R.string.post_writing_image_permission_denied_message))
                }
            }

            else -> Unit
        }
    }

    private fun navigateToBack() {
        finish()
    }

    companion object {
        private const val MAX_IMAGE_COUNT = 5
        private const val REQUEST_STORAGE = 1000

        fun startActivity(context: Context, eventId: Long) {
            val intent = Intent(context, PostWritingActivity::class.java)
                .putExtra(EVENT_ID_KEY, eventId)
            context.startActivity(intent)
        }
    }
}
