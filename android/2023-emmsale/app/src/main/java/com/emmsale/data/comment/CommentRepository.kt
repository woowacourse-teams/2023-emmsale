package com.emmsale.data.comment

import com.emmsale.data.common.ApiResult

interface CommentRepository {

    suspend fun getComments(eventId: Long): ApiResult<List<Comment>>

    suspend fun getChildComments(commentId: Long): ApiResult<List<Comment>>

    suspend fun saveComment(content: String, eventId: Long, parentId: Long? = null): ApiResult<Unit>
}