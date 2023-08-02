package com.emmsale.data.comment

import com.emmsale.data.comment.dto.CommentApiModel
import com.emmsale.data.comment.dto.CommentFamilyApiModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentService {

    @GET("comments")
    suspend fun getComments(@Query("eventId") eventId: Long): Response<List<CommentFamilyApiModel>>

    @GET("comments/{commentId}/children")
    suspend fun getChildComments(@Path("commentId") commentId: Long): Response<List<CommentApiModel>>
}