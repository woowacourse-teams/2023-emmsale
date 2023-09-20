package com.emmsale.data.repository.concretes

import com.emmsale.data.apiModel.request.CommentCreateRequest
import com.emmsale.data.apiModel.request.CommentQueryRequest
import com.emmsale.data.apiModel.request.CommentReportCreateRequest
import com.emmsale.data.apiModel.request.CommentUpdateRequest
import com.emmsale.data.apiModel.response.CommentFamilyApiModel
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.Comment
import com.emmsale.data.repository.interfaces.CommentRepository
import com.emmsale.data.service.CommentService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class DefaultCommentRepository(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val commentService: CommentService,
) : CommentRepository {

    private val comments: List<Comment> = listOf(
        Comment(
            id = 1,
            eventId = 1,
            eventName = "인프콘 2023",
            authorId = 34,
            authorName = "토마스",
            authorImageUrl = "https://avatars.githubusercontent.com/u/123928686?v=4",
            parentId = null,
            content = "알아내면 알려주세요.",
            createdAt = LocalDateTime.of(2023, 9, 20, 4, 6),
            updatedAt = LocalDateTime.of(2023, 9, 20, 4, 6),
            deleted = false,
            childComments = listOf(
                Comment(
                    id = 2,
                    eventId = 1,
                    eventName = "인프콘 2023",
                    authorId = 25,
                    authorName = "Buna",
                    authorImageUrl = "https://avatars.githubusercontent.com/u/123928686?v=4",
                    parentId = 1L,
                    content = "싫은데요?",
                    createdAt = LocalDateTime.of(2023, 9, 20, 4, 10),
                    updatedAt = LocalDateTime.of(2023, 9, 20, 4, 10),
                    deleted = false,
                    childComments = emptyList(),
                ),
                Comment(
                    id = 3,
                    eventId = 1,
                    eventName = "인프콘 2023",
                    authorId = 34,
                    authorName = "토마스",
                    authorImageUrl = "https://avatars.githubusercontent.com/u/123928686?v=4",
                    parentId = 1L,
                    content = "싫으면 시집 가세요",
                    createdAt = LocalDateTime.of(2023, 9, 20, 4, 11),
                    updatedAt = LocalDateTime.of(2023, 9, 20, 4, 11),
                    deleted = false,
                    childComments = emptyList(),
                ),
                Comment(
                    id = 4,
                    eventId = 1,
                    eventName = "인프콘 2023",
                    authorId = 25,
                    authorName = "Buna",
                    authorImageUrl = "https://avatars.githubusercontent.com/u/123928686?v=4",
                    parentId = 1L,
                    content = "ㅠㅠ",
                    createdAt = LocalDateTime.of(2023, 9, 20, 4, 12),
                    updatedAt = LocalDateTime.of(2023, 9, 20, 4, 12),
                    deleted = false,
                    childComments = emptyList(),
                ),
                Comment(
                    id = 5,
                    eventId = 1,
                    eventName = "인프콘 2023",
                    authorId = 34,
                    authorName = "토마스",
                    authorImageUrl = "https://avatars.githubusercontent.com/u/123928686?v=4",
                    parentId = 1L,
                    content = "ㅠㅠ",
                    createdAt = LocalDateTime.of(2023, 9, 20, 4, 12),
                    updatedAt = LocalDateTime.of(2023, 9, 20, 4, 13),
                    deleted = true,
                    childComments = emptyList(),
                ),
                Comment(
                    id = 6,
                    eventId = 1,
                    eventName = "인프콘 2023",
                    authorId = 25,
                    authorName = "Buna",
                    authorImageUrl = "https://avatars.githubusercontent.com/u/123928686?v=4",
                    parentId = 1L,
                    content = "뭐라고 하셨나요?",
                    createdAt = LocalDateTime.of(2023, 9, 20, 4, 15),
                    updatedAt = LocalDateTime.of(2023, 9, 20, 4, 15),
                    deleted = false,
                    childComments = emptyList(),
                ),
            ),
        ),
        Comment(
            id = 1,
            eventId = 1,
            eventName = "인프콘 2023",
            authorId = 34,
            authorName = "토마스",
            authorImageUrl = "https://avatars.githubusercontent.com/u/123928686?v=4",
            parentId = null,
            content = "알아내면 알려주세요.",
            createdAt = LocalDateTime.of(2023, 9, 20, 4, 6),
            updatedAt = LocalDateTime.of(2023, 9, 20, 4, 6),
            deleted = false,
            childComments = listOf(
                Comment(
                    id = 2,
                    eventId = 1,
                    eventName = "인프콘 2023",
                    authorId = 25,
                    authorName = "Buna",
                    authorImageUrl = "https://avatars.githubusercontent.com/u/123928686?v=4",
                    parentId = 1L,
                    content = "싫은데요?",
                    createdAt = LocalDateTime.of(2023, 9, 20, 4, 10),
                    updatedAt = LocalDateTime.of(2023, 9, 20, 4, 10),
                    deleted = false,
                    childComments = emptyList(),
                ),
                Comment(
                    id = 3,
                    eventId = 1,
                    eventName = "인프콘 2023",
                    authorId = 34,
                    authorName = "토마스",
                    authorImageUrl = "https://avatars.githubusercontent.com/u/123928686?v=4",
                    parentId = 1L,
                    content = "싫으면 시집 가세요",
                    createdAt = LocalDateTime.of(2023, 9, 20, 4, 11),
                    updatedAt = LocalDateTime.of(2023, 9, 20, 4, 11),
                    deleted = false,
                    childComments = emptyList(),
                ),
                Comment(
                    id = 4,
                    eventId = 1,
                    eventName = "인프콘 2023",
                    authorId = 25,
                    authorName = "Buna",
                    authorImageUrl = "https://avatars.githubusercontent.com/u/123928686?v=4",
                    parentId = 1L,
                    content = "ㅠㅠ",
                    createdAt = LocalDateTime.of(2023, 9, 20, 4, 12),
                    updatedAt = LocalDateTime.of(2023, 9, 20, 4, 12),
                    deleted = false,
                    childComments = emptyList(),
                ),
                Comment(
                    id = 5,
                    eventId = 1,
                    eventName = "인프콘 2023",
                    authorId = 34,
                    authorName = "토마스",
                    authorImageUrl = "https://avatars.githubusercontent.com/u/123928686?v=4",
                    parentId = 1L,
                    content = "ㅠㅠ",
                    createdAt = LocalDateTime.of(2023, 9, 20, 4, 12),
                    updatedAt = LocalDateTime.of(2023, 9, 20, 4, 13),
                    deleted = true,
                    childComments = emptyList(),
                ),
                Comment(
                    id = 6,
                    eventId = 1,
                    eventName = "인프콘 2023",
                    authorId = 25,
                    authorName = "Buna",
                    authorImageUrl = "https://avatars.githubusercontent.com/u/123928686?v=4",
                    parentId = 1L,
                    content = "뭐라고 하셨나요?",
                    createdAt = LocalDateTime.of(2023, 9, 20, 4, 15),
                    updatedAt = LocalDateTime.of(2023, 9, 20, 4, 15),
                    deleted = false,
                    childComments = emptyList(),
                ),
            ),
        ),
        Comment(
            id = 1,
            eventId = 1,
            eventName = "인프콘 2023",
            authorId = 34,
            authorName = "토마스",
            authorImageUrl = "https://avatars.githubusercontent.com/u/123928686?v=4",
            parentId = null,
            content = "알아내면 알려주세요.",
            createdAt = LocalDateTime.of(2023, 9, 20, 4, 6),
            updatedAt = LocalDateTime.of(2023, 9, 20, 4, 6),
            deleted = true,
            childComments = listOf(
                Comment(
                    id = 2,
                    eventId = 1,
                    eventName = "인프콘 2023",
                    authorId = 25,
                    authorName = "Buna",
                    authorImageUrl = "https://avatars.githubusercontent.com/u/123928686?v=4",
                    parentId = 1L,
                    content = "싫은데요?",
                    createdAt = LocalDateTime.of(2023, 9, 20, 4, 10),
                    updatedAt = LocalDateTime.of(2023, 9, 20, 4, 10),
                    deleted = false,
                    childComments = emptyList(),
                ),
                Comment(
                    id = 3,
                    eventId = 1,
                    eventName = "인프콘 2023",
                    authorId = 34,
                    authorName = "토마스",
                    authorImageUrl = "https://avatars.githubusercontent.com/u/123928686?v=4",
                    parentId = 1L,
                    content = "싫으면 시집 가세요",
                    createdAt = LocalDateTime.of(2023, 9, 20, 4, 11),
                    updatedAt = LocalDateTime.of(2023, 9, 20, 4, 11),
                    deleted = false,
                    childComments = emptyList(),
                ),
                Comment(
                    id = 4,
                    eventId = 1,
                    eventName = "인프콘 2023",
                    authorId = 25,
                    authorName = "Buna",
                    authorImageUrl = "https://avatars.githubusercontent.com/u/123928686?v=4",
                    parentId = 1L,
                    content = "ㅠㅠ",
                    createdAt = LocalDateTime.of(2023, 9, 20, 4, 12),
                    updatedAt = LocalDateTime.of(2023, 9, 20, 4, 12),
                    deleted = false,
                    childComments = emptyList(),
                ),
                Comment(
                    id = 5,
                    eventId = 1,
                    eventName = "인프콘 2023",
                    authorId = 34,
                    authorName = "토마스",
                    authorImageUrl = "https://avatars.githubusercontent.com/u/123928686?v=4",
                    parentId = 1L,
                    content = "ㅠㅠ",
                    createdAt = LocalDateTime.of(2023, 9, 20, 4, 12),
                    updatedAt = LocalDateTime.of(2023, 9, 20, 4, 13),
                    deleted = true,
                    childComments = emptyList(),
                ),
                Comment(
                    id = 6,
                    eventId = 1,
                    eventName = "인프콘 2023",
                    authorId = 25,
                    authorName = "Buna",
                    authorImageUrl = "https://avatars.githubusercontent.com/u/123928686?v=4",
                    parentId = 1L,
                    content = "뭐라고 하셨나요?",
                    createdAt = LocalDateTime.of(2023, 9, 20, 4, 15),
                    updatedAt = LocalDateTime.of(2023, 9, 20, 4, 15),
                    deleted = false,
                    childComments = emptyList(),
                ),
            ),
        ),
    )

    override suspend fun getComments(
        feedId: Long,
    ): ApiResponse<List<Comment>> = withContext(dispatcher) {
        // commentService
        //     .getComments(CommentQueryRequest(feedId = feedId))
        //     .map(List<CommentFamilyApiModel>::toData)
        Success(comments)
    }

    override suspend fun getCommentsByMemberId(
        memberId: Long,
    ): ApiResponse<List<Comment>> = withContext(dispatcher) {
        commentService
            .getComments(CommentQueryRequest(memberId = memberId))
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
