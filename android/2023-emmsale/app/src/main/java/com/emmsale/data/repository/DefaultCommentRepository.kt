package com.emmsale.data.repository

import com.emmsale.data.apiModel.request.ReportRequestBody
import com.emmsale.data.apiModel.request.SaveCommentRequestBody
import com.emmsale.data.apiModel.request.UpdateCommentRequestBody
import com.emmsale.data.apiModel.response.CommentFamilyApiModel
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.Comment
import com.emmsale.data.service.CommentService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultCommentRepository(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val commentService: CommentService,
) : CommentRepository {

    override suspend fun getComments(eventId: Long): ApiResult<List<Comment>> =
        withContext(dispatcher) {
            handleApi(
                execute = { commentService.getComments(eventId) },
                mapToDomain = List<CommentFamilyApiModel>::toData,
            )
        }

    override suspend fun getCommentsByMemberId(memberId: Long): ApiResult<List<Comment>> =
        withContext(dispatcher) {
            handleApi(
                execute = { commentService.getCommentsByMemberId(memberId) },
                mapToDomain = List<CommentFamilyApiModel>::toData,
            )
        }

    override suspend fun getComment(commentId: Long): ApiResult<Comment> = withContext(dispatcher) {
        handleApi(
            execute = { commentService.getComment(commentId) },
            mapToDomain = CommentFamilyApiModel::toData,
        )
    }

    override suspend fun saveComment(
        content: String,
        eventId: Long,
        parentId: Long?,
    ): ApiResult<Unit> = withContext(dispatcher) {
        handleApi(
            execute = {
                commentService.saveComment(
                    getSaveCommentRequestBody(content, eventId, parentId),
                )
            },
            mapToDomain = {},
        )
    }

    private fun getSaveCommentRequestBody(
        content: String,
        eventId: Long,
        parentId: Long?,
    ) = SaveCommentRequestBody(
        content = content,
        eventId = eventId,
        parentId = parentId,
    )

    override suspend fun updateComment(commentId: Long, content: String): ApiResult<Unit> =
        withContext(dispatcher) {
            handleApi(
                execute = {
                    commentService.updateComment(
                        commentId,
                        UpdateCommentRequestBody(content),
                    )
                },
                mapToDomain = {},
            )
        }

    override suspend fun deleteComment(commentId: Long): ApiResult<Unit> = withContext(dispatcher) {
        handleApi(
            execute = { commentService.deleteComment(commentId) },
            mapToDomain = {},
        )
    }

    override suspend fun reportComment(
        commentId: Long,
        authorId: Long,
        reporterId: Long,
    ): ApiResult<Unit> = withContext(dispatcher) {
        handleApi(
            execute = {
                commentService.reportComment(
                    ReportRequestBody.createCommentReport(
                        commentId = commentId,
                        authorId = authorId,
                        reporterId = reporterId,
                    ),
                )
            },
            mapToDomain = {},
        )
    }
}
