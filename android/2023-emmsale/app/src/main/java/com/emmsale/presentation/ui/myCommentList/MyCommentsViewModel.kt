package com.emmsale.presentation.ui.myCommentList

import com.emmsale.data.model.Comment
import com.emmsale.data.repository.interfaces.CommentRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class MyCommentsViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val commentRepository: CommentRepository,
) : RefreshableViewModel() {

    private val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _comments = NotNullMutableLiveData(listOf<Comment>())
    val comments: NotNullLiveData<List<Comment>> = _comments

    init {
        fetchMyComments()
    }

    private fun fetchMyComments(): Job = fetchData(
        fetchData = { commentRepository.getCommentsByMemberId(uid) },
        onSuccess = {
            _comments.value = it.extractMyComments().reversed()
        },
    )

    override fun refresh(): Job = refreshData(
        refresh = { commentRepository.getCommentsByMemberId(uid) },
        onSuccess = {
            _comments.value = it.extractMyComments().reversed()
        },
    )

    private fun List<Comment>.extractMyComments(): List<Comment> =
        flatMap { comment ->
            listOf(comment) + comment.childComments
        }.filter { comment -> comment.writer.id == uid && !comment.isDeleted }
}
