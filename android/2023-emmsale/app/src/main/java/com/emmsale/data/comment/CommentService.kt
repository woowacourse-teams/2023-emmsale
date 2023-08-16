package com.emmsale.data.comment

import com.emmsale.data.comment.dto.CommentApiModel
import com.emmsale.data.comment.dto.CommentFamilyApiModel
import com.emmsale.data.comment.dto.SaveCommentRequestBody
import com.emmsale.data.comment.dto.UpdateCommentRequestBody
import com.emmsale.data.report.dto.ReportApiModel
import com.emmsale.data.report.dto.ReportRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentService {

    @GET("comments")
    suspend fun getComments(@Query("eventId") eventId: Long): Response<List<CommentFamilyApiModel>>

    @GET("comments")
    suspend fun getCommentsByMemberId(@Query("memberId") memberId: Long): Response<List<CommentFamilyApiModel>>

    @GET("comments/{commentId}")
    suspend fun getComment(@Path("commentId") commentId: Long): Response<CommentFamilyApiModel>

    @GET("comments/{commentId}/children")
    suspend fun getChildComments(@Path("commentId") commentId: Long): Response<List<CommentApiModel>>

    @POST("comments")
    suspend fun saveComment(@Body saveCommentRequestBody: SaveCommentRequestBody): Response<CommentApiModel>

    @PATCH("comments/{commentId}")
    suspend fun updateComment(
        @Path("commentId") commentId: Long,
        @Body updateCommentRequestBody: UpdateCommentRequestBody,
    ): Response<CommentApiModel>

    @DELETE("comments/{commentId}")
    suspend fun deleteComment(@Path("commentId") commentId: Long): Response<Unit>

    @POST("/reports")
    suspend fun reportComment(@Body reportRequestBody: ReportRequestBody): Response<ReportApiModel>
}
