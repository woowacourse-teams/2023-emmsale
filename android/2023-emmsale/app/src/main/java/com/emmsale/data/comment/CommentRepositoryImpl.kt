package com.emmsale.data.comment

import com.emmsale.data.comment.dto.SaveCommentRequestBody
import com.emmsale.data.comment.dto.UpdateCommentRequestBody
import com.emmsale.data.comment.mapper.toData
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.handleApi
import com.emmsale.data.member.MemberRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class CommentRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val commentService: CommentService,
    private val memberRepository: MemberRepository,
) : CommentRepository {

    override suspend fun getComments(eventId: Long): ApiResult<List<Comment>> =
        withContext(dispatcher) {
            val commentsResponse = commentService.getComments(eventId)

            val commentsApiModels = commentsResponse.body() ?: return@withContext ApiError(
                commentsResponse.code(), commentsResponse.errorBody().toString()
            )

            val comments = commentsApiModels.map { commentFamilyApiModel ->
                val parentComment = commentFamilyApiModel.parentComment
                val childComments = commentFamilyApiModel.childComments
                val parentCommentAuthor =
                    async { memberRepository.getMember(parentComment.memberId) }
                val childCommentAuthors = childComments.map {
                    async { memberRepository.getMember(it.memberId) }
                }
                val authorResults =
                    awaitAll(parentCommentAuthor, *childCommentAuthors.toTypedArray())
                authorResults.forEach {
                    when (it) {
                        is ApiError -> return@withContext ApiError(it.code, it.message)
                        is ApiException -> return@withContext ApiException(it.e)
                        else -> {}
                    }
                }
                val authors = authorResults.map { it as ApiSuccess }.map { it.data }
                val parentAuthor = authors[0]
                val childAuthors = authors.subList(1, authors.size)
                parentComment.toData(parentAuthor, childComments, childAuthors)
            }

            ApiSuccess(comments)
        }

    override suspend fun getChildComments(commentId: Long): ApiResult<List<Comment>> =
        withContext(dispatcher) {
            val childCommentsResponse = commentService.getChildComments(commentId)

            val childCommentApiModels = childCommentsResponse.body() ?: return@withContext ApiError(
                childCommentsResponse.code(), childCommentsResponse.message()
            )

            val authorsDeferred = childCommentApiModels.map {
                async { memberRepository.getMember(it.memberId) }
            }
            val authorResults = awaitAll(*authorsDeferred.toTypedArray())
            authorResults.forEach {
                when (it) {
                    is ApiError -> return@withContext ApiError(it.code, it.message)
                    is ApiException -> return@withContext ApiException(it.e)
                    else -> {}
                }
            }
            val authors = authorResults.map { it as ApiSuccess }.map { it.data }
            val childComments = childCommentApiModels.zip(authors) { commentApiModel, author ->
                commentApiModel.toData(author)
            }
            ApiSuccess(childComments)
        }

    override suspend fun saveComment(
        content: String,
        eventId: Long,
        parentId: Long?,
    ): ApiResult<Unit> = withContext(dispatcher) {
        handleApi(
            commentService.saveComment(
                SaveCommentRequestBody(
                    content = content,
                    eventId = eventId,
                    parentId = parentId
                )
            )
        ) { }
    }

    override suspend fun updateComment(commentId: Long, content: String): ApiResult<Unit> =
        withContext(dispatcher) {
            handleApi(commentService.updateComment(commentId, UpdateCommentRequestBody(content))) {}
        }

    override suspend fun deleteComment(commentId: Long): ApiResult<Unit> = withContext(dispatcher) {
        handleApi(commentService.deleteComment(commentId)) {}
    }
}