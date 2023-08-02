package com.emmsale.data.comment

import com.emmsale.data.comment.mapper.toData
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.member.MemberRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class CommentRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val commentService: CommentService,
    private val memberRepository: MemberRepository
) : CommentRepository{

    override suspend fun getComments(eventId: Long): ApiResult<List<Comment>> = withContext(dispatcher) {
        val commentsResponse = commentService.getComments(eventId)

        val commentsApiModels = commentsResponse.body() ?: return@withContext ApiError(
            commentsResponse.code(), commentsResponse.errorBody().toString()
        )

        val comments = commentsApiModels.map { commentFamilyApiModel ->
            val parentComment = commentFamilyApiModel.parentComment
            val childComments = commentFamilyApiModel.childComments
            val parentCommentAuthor = async { memberRepository.getMember(parentComment.memberId) }
            val childCommentAuthors = childComments.map {
                async { memberRepository.getMember(it.memberId) }
            }
            val authorResults = awaitAll(parentCommentAuthor, *childCommentAuthors.toTypedArray())
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
}