package com.emmsale.data.comment

import com.emmsale.data.comment.dto.CommentFamilyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CommentService {

    @GET("comments")
    suspend fun getComments(@Query("eventId") eventId: Long): Response<List<CommentFamilyResponse>>
}