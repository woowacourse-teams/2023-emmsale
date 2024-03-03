package com.emmsale.data.repository.interfaces

import com.emmsale.data.network.callAdapter.ApiResponse
import com.emmsale.model.Comment

interface CommentRepository {

    suspend fun getComments(feedId: Long): ApiResponse<List<Comment>>

    suspend fun getCommentsByMemberId(memberId: Long): ApiResponse<List<Comment>>

    suspend fun getComment(commentId: Long): ApiResponse<Comment>

    suspend fun saveComment(
        content: String,
        feedId: Long,
        parentId: Long? = null,
    ): ApiResponse<Unit>

    suspend fun updateComment(commentId: Long, content: String): ApiResponse<Unit>

    suspend fun deleteComment(commentId: Long): ApiResponse<Unit>

    suspend fun reportComment(commentId: Long, authorId: Long, reporterId: Long): ApiResponse<Unit>
}
