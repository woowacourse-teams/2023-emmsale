package com.emmsale.data.repository

import com.emmsale.data.common.ApiResult
import com.emmsale.data.model.Comment

interface CommentRepository {

    suspend fun getComments(eventId: Long): ApiResult<List<Comment>>

    suspend fun getCommentsByMemberId(memberId: Long): ApiResult<List<Comment>>

    suspend fun getComment(commentId: Long): ApiResult<Comment>

    suspend fun saveComment(content: String, eventId: Long, parentId: Long? = null): ApiResult<Unit>

    suspend fun updateComment(commentId: Long, content: String): ApiResult<Unit>

    suspend fun deleteComment(commentId: Long): ApiResult<Unit>

    suspend fun reportComment(commentId: Long, authorId: Long, reporterId: Long): ApiResult<Unit>
}