package com.emmsale.presentation.ui.postList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentPostListBinding
import com.emmsale.presentation.base.BaseFragment

class PostListFragment() : BaseFragment<FragmentPostListBinding>() {

    override val layoutResId: Int = R.layout.fragment_post_list

    private val eventId: Long by lazy {
        arguments?.getLong(EVENT_ID_KEY) ?: throw IllegalArgumentException(
            EVENT_ID_NULL_ERROR,
        )
    }

    private val viewModel: PostListViewModel by viewModels {
        PostListViewModel.factory(eventId = eventId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }

    companion object {
        private const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val EVENT_ID_NULL_ERROR = "행사 아이디를 가져오지 못했어요"

        fun create(eventId: Long): PostListFragment {
            val fragment = PostListFragment()
            fragment.arguments = Bundle().apply {
                putLong(EVENT_ID_KEY, eventId)
            }
            return fragment
        }
    }
}
