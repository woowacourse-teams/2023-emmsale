package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.request.ChildCommentCreateRequest
import com.emmsale.data.apiModel.request.CommentReportCreateRequest
import com.emmsale.data.apiModel.request.CommentUpdateRequest
import com.emmsale.data.apiModel.response.CommentFamilyApiModel
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.Comment
import com.emmsale.data.repository.interfaces.CommentRepository
import com.emmsale.data.service.CommentService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultCommentRepository(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val commentService: CommentService,
) : CommentRepository {

    override suspend fun getComments(
        eventId: Long,
    ): ApiResponse<List<Comment>> = withContext(dispatcher) {
        commentService
            .getComments(eventId)
            .map(List<CommentFamilyApiModel>::toData)
    }

    override suspend fun getCommentsByMemberId(
        memberId: Long,
    ): ApiResponse<List<Comment>> = withContext(dispatcher) {
        commentService
            .getCommentsByMemberId(memberId)
            .map(List<CommentFamilyApiModel>::toData)
    }

    override suspend fun getComment(
        commentId: Long,
    ): ApiResponse<Comment> = withContext(dispatcher) {
        commentService
            .getComment(commentId)
            .map(CommentFamilyApiModel::toData)
    }

    override suspend fun saveComment(
        content: String,
        eventId: Long,
        parentId: Long?,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        commentService
            .saveComment(getSaveCommentRequestBody(content, eventId, parentId))
            .map { }
    }

    private fun getSaveCommentRequestBody(
        content: String,
        eventId: Long,
        parentId: Long?,
    ) = ChildCommentCreateRequest(
        content = content,
        eventId = eventId,
        parentId = parentId,
    )

    override suspend fun updateComment(
        commentId: Long,
        content: String,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        commentService
            .updateComment(commentId, CommentUpdateRequest(content))
            .map { }
    }

    override suspend fun deleteComment(
        commentId: Long,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        commentService.deleteComment(commentId)
    }

    override suspend fun reportComment(
        commentId: Long,
        authorId: Long,
        reporterId: Long,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        commentService.reportComment(
            CommentReportCreateRequest.createCommentReport(
                commentId = commentId,
                authorId = authorId,
                reporterId = reporterId,
            ),
        ).map { }
    }
}
