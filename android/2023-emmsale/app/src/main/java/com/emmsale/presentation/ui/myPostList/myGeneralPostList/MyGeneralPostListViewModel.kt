package com.emmsale.presentation.ui.myPostList.myGeneralPostList

import androidx.lifecycle.ViewModel
import com.emmsale.data.repository.interfaces.PostRepository
import javax.inject.Inject

class MyGeneralPostListViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel()
