package com.emmsale.presentation.ui.postWriting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityPostWritingBinding
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.postWriting.recyclerView.PostWritingImageAdapter
import com.emmsale.presentation.ui.postWriting.uiState.PostUploadResultUiState

class PostWritingActivity : AppCompatActivity() {

    private val binding: ActivityPostWritingBinding by lazy {
        ActivityPostWritingBinding.inflate(layoutInflater)
    }

    private val eventId: Long by lazy {
        intent.getLongExtra(EVENT_ID_KEY, DEFAULT_ID)
    }

    private val viewModel: PostWritingViewModel by viewModels {
        PostWritingViewModel.viewModelFactory(eventId)
    }

    private val adapter: PostWritingImageAdapter by lazy {
        PostWritingImageAdapter(deleteImage = viewModel::deleteImageUrl)
    }

    private val photoPicker = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(MAX_IMAGE_COUNT),
    ) { uris ->
        viewModel.fetchImageUrls(uris.map { it.toString() })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.rvPostWritingImageList.adapter = adapter
        setUpBinding()
        setUpPostUploadResult()
        setUpImageUrls()
    }

    private fun setUpBinding() {
        setContentView(binding.root)
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
                showToast(content.responseId.toString())
                finish()
            }

            FetchResult.LOADING -> binding.progressBarPostWriting.visibility = View.VISIBLE

            FetchResult.ERROR -> {
                binding.progressBarPostWriting.visibility = View.GONE
                showToast(getString(R.string.post_writing_upload_error))
            }
        }
    }

    private fun setUpImageUrls() {
        viewModel.imageUrls.observe(this) {
            adapter.submitList(it)
        }
    }

    private fun showAlbum() {
        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun uploadPost() {
        viewModel.uploadPost(
            title = binding.etPostWritingTitle.text.toString(),
            content = binding.etPostWritingContent.text.toString(),
        )
    }

    private fun navigateToBack() {
        finish()
    }

    companion object {
        private const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val DEFAULT_ID = -1L
        private const val MAX_IMAGE_COUNT = 5

        fun startActivity(context: Context, eventId: Long) {
            val intent = Intent(context, PostWritingActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            context.startActivity(intent)
        }
    }
}
