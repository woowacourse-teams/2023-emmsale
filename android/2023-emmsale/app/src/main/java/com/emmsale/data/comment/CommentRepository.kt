package com.emmsale.data.comment

import com.emmsale.data.common.ApiResult

interface CommentRepository {

    suspend fun getComments(eventId: Long): ApiResult<List<Comment>>

    suspend fun getComment(commentId: Long): ApiResult<Comment>

    suspend fun saveComment(content: String, eventId: Long, parentId: Long? = null): ApiResult<Unit>

    suspend fun updateComment(commentId: Long, content: String): ApiResult<Unit>

    suspend fun deleteComment(commentId: Long): ApiResult<Unit>
}
