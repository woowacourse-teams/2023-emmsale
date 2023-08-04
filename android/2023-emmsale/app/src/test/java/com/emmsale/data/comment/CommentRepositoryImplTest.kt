package com.emmsale.data.comment

import com.emmsale.data.comment.dto.CommentApiModel
import com.emmsale.data.comment.dto.SaveCommentRequestBody
import com.emmsale.data.comment.dto.UpdateCommentRequestBody
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.member.MemberRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import retrofit2.Response

internal class CommentRepositoryImplTest {

    private lateinit var commentService: CommentService
    private lateinit var memberRepository: MemberRepository
    private lateinit var sut: CommentRepositoryImpl

    @BeforeEach
    fun setUp() {
        commentService = mockk()
        memberRepository = mockk()
        sut = CommentRepositoryImpl(
            commentService = commentService,
        )
    }

    @Test
    @DisplayName("네트워크 통신이 원활해 댓글 게시에 성공하면 ApiSuccess 객체를 반환한다")
    fun test3() = runTest {
        val eventId = 1L
        val content = "ㅎㅇㅎㅇ"
        val parentId: Long? = null
        val saveCommentRequestBody = SaveCommentRequestBody(content, eventId, parentId)
        val apiModel = CommentApiModel(
            commentId = 4,
            memberId = 1,
            memberName = "홍길동",
            memberImageUrl = "https://naver.com",
            content = content,
            parentId = null,
            eventId = eventId,
            createdAt = "2023:07:25:22:01:05",
            updatedAt = "2023:07:25:22:01:05",
            deleted = false
        )
        coEvery { commentService.saveComment(saveCommentRequestBody) } returns Response.success(
            apiModel
        )

        val result = sut.saveComment(content, eventId, parentId)

        assertAll(
            { assertThat(result).isInstanceOf(ApiSuccess::class.java) },
            { assertThat((result as ApiSuccess).data).isEqualTo(Unit) }
        )
    }

    @Test
    @DisplayName("네트워크 통신이 원활해 댓글 수정에 성공하면 ApiSuccess 객체를 반환한다")
    fun test4() = runTest {
        val commentId = 1L
        val content = "ㅎㅇㅎㅇ"
        val apiModel = CommentApiModel(
            commentId = 4,
            memberId = 1,
            memberName = "홍길동",
            memberImageUrl = "https://naver.com",
            content = content,
            parentId = null,
            eventId = 1,
            createdAt = "2023:07:25:22:01:05",
            updatedAt = "2023:07:25:22:01:05",
            deleted = false
        )
        coEvery {
            commentService.updateComment(commentId, UpdateCommentRequestBody(content))
        } returns Response.success(apiModel)

        val result = sut.updateComment(commentId, content)

        assertAll(
            { assertThat(result).isInstanceOf(ApiSuccess::class.java) },
            { assertThat((result as ApiSuccess).data).isEqualTo(Unit) }
        )
    }

    @Test
    @DisplayName("네트워크 통신이 원활해 댓글 삭제에 성공하면 ApiSuccess 객체를 반환한다")
    fun test5() = runTest {
        val commentId = 1L
        coEvery { commentService.deleteComment(commentId) } returns Response.success(Unit)

        val result = sut.deleteComment(commentId)

        assertAll(
            { assertThat(result).isInstanceOf(ApiSuccess::class.java) },
            { assertThat((result as ApiSuccess).data).isEqualTo(Unit) }
        )
    }
}
