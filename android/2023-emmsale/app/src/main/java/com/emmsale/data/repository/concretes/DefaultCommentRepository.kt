package com.emmsale.data.repository.concretes

import com.emmsale.data.network.apiModel.request.CommentCreateRequest
import com.emmsale.data.network.apiModel.request.CommentReportCreateRequest
import com.emmsale.data.network.apiModel.request.CommentUpdateRequest
import com.emmsale.data.network.apiModel.response.CommentFamilyApiModel
import com.emmsale.data.network.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.model.Comment
import com.emmsale.data.repository.interfaces.CommentRepository
import com.emmsale.data.network.service.CommentService
import com.emmsale.data.network.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultCommentRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val commentService: CommentService,
) : CommentRepository {

    override suspend fun getComments(
        feedId: Long,
    ): ApiResponse<List<Comment>> = withContext(dispatcher) {
        commentService
            .getComments(feedId = feedId)
            .map(List<CommentFamilyApiModel>::toData)
    }

    override suspend fun getCommentsByMemberId(
        memberId: Long,
    ): ApiResponse<List<Comment>> = withContext(dispatcher) {
        commentService
            .getComments(memberId = memberId)
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
        feedId: Long,
        parentId: Long?,
    ): ApiResponse<Unit> = withContext(dispatcher) {
        val commentCreateRequest = CommentCreateRequest(
            content = content,
            feedId = feedId,
            parentId = parentId,
        )

        commentService
            .saveComment(commentCreateRequest)
            .map { }
    }

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
