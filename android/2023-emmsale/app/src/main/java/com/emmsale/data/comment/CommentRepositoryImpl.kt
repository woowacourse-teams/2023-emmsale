package com.emmsale.data.comment

import com.emmsale.data.comment.dto.CommentFamilyApiModel
import com.emmsale.data.comment.dto.SaveCommentRequestBody
import com.emmsale.data.comment.dto.UpdateCommentRequestBody
import com.emmsale.data.comment.mapper.toData
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CommentRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val commentService: CommentService,
) : CommentRepository {

    override suspend fun getComments(eventId: Long): ApiResult<List<Comment>> =
        withContext(dispatcher) {
            handleApi(commentService.getComments(eventId), List<CommentFamilyApiModel>::toData)
        }

    override suspend fun getComment(commentId: Long): ApiResult<Comment> = withContext(dispatcher) {
        handleApi(commentService.getComment(commentId), CommentFamilyApiModel::toData)
    }

    override suspend fun saveComment(
        content: String,
        eventId: Long,
        parentId: Long?,
    ): ApiResult<Unit> = withContext(dispatcher) {
        handleApi(
            commentService.saveComment(
                SaveCommentRequestBody(
                    content = content,
                    eventId = eventId,
                    parentId = parentId
                )
            )
        ) { }
    }

    override suspend fun updateComment(commentId: Long, content: String): ApiResult<Unit> =
        withContext(dispatcher) {
            handleApi(commentService.updateComment(commentId, UpdateCommentRequestBody(content))) {}
        }

    override suspend fun deleteComment(commentId: Long): ApiResult<Unit> = withContext(dispatcher) {
        handleApi(commentService.deleteComment(commentId)) {}
    }
}
