package com.emmsale.data.comment

import com.emmsale.data.comment.dto.CommentApiModel
import com.emmsale.data.comment.dto.CommentFamilyApiModel
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.member.Member1
import com.emmsale.data.member.MemberRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import retrofit2.Response
import java.time.LocalDateTime

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
            memberRepository = memberRepository
        )
    }

    @Test
    @DisplayName("네트워크 통신이 원활하면 ApiModel을 파싱한 특정 행사에 달린 댓글들을 불러올 수 있다")
    fun test1() = runTest {
        val eventId = 1L
        val memberId = 1L
        val apiModels = listOf(
            CommentFamilyApiModel(
                CommentApiModel(
                    commentId = 4,
                    memberId = memberId,
                    memberName = "홍길동",
                    memberImageUrl = "https://naver.com",
                    content = "부모댓글2",
                    parentId = null,
                    eventId = 1,
                    createdAt = "2023:07:25:22:01:05",
                    updatedAt = "2023:07:25:22:01:05",
                    deleted = false
                ),
                listOf()
            ),
            CommentFamilyApiModel(
                CommentApiModel(
                    commentId = 5,
                    memberId = memberId,
                    memberName = "홍길동",
                    memberImageUrl = "https://naver.com",
                    content = "부모댓글1",
                    parentId = null,
                    eventId = 1,
                    createdAt = "2023:07:25:22:01:05",
                    updatedAt = "2023:07:25:22:01:05",
                    deleted = false
                ),
                listOf(
                    CommentApiModel(
                        commentId = 2,
                        memberId = memberId,
                        memberName = "홍길동",
                        memberImageUrl = "https://naver.com",
                        content = "부모댓글1에 대한 자식댓글1",
                        parentId = 1,
                        eventId = 1,
                        createdAt = "2023:07:25:22:01:05",
                        updatedAt = "2023:07:25:22:01:05",
                        deleted = false
                    ),
                    CommentApiModel(
                        commentId = 3,
                        memberId = memberId,
                        memberName = "홍길동",
                        memberImageUrl = "https://naver.com",
                        content = "부모댓글1에 대한 자식댓글2",
                        parentId = 1,
                        eventId = 1,
                        createdAt = "2023:07:25:22:01:05",
                        updatedAt = "2023:07:25:22:01:05",
                        deleted = false
                    )
                )
            )
        )
        coEvery { commentService.getComments(eventId) } returns Response.success(apiModels)
        val member = Member1(
            id = memberId,
            name = "홍길동",
            description = "",
            imageUrl = "https://naver.com",
            activities = listOf()
        )
        coEvery { memberRepository.getMember(memberId) } returns ApiSuccess(member)

        val result = sut.getComments(eventId)

        assertAll(
            { assertThat(result).isInstanceOf(ApiSuccess::class.java) },
            {
                assertThat((result as ApiSuccess).data).isEqualTo(
                    listOf(
                        Comment(
                            id = 4,
                            author = member,
                            content = "부모댓글2",
                            createdAt = LocalDateTime.of(2023, 7, 25, 22, 1, 5),
                            updatedAt = LocalDateTime.of(2023, 7, 25, 22, 1, 5),
                            deleted = false,
                            childComments = listOf()
                        ),
                        Comment(
                            id = 5,
                            author = member,
                            content = "부모댓글1",
                            createdAt = LocalDateTime.of(2023, 7, 25, 22, 1, 5),
                            updatedAt = LocalDateTime.of(2023, 7, 25, 22, 1, 5),
                            deleted = false,
                            childComments = listOf(
                                Comment(
                                    id = 2,
                                    author = member,
                                    content = "부모댓글1에 대한 자식댓글1",
                                    createdAt = LocalDateTime.of(2023, 7, 25, 22, 1, 5),
                                    updatedAt = LocalDateTime.of(2023, 7, 25, 22, 1, 5),
                                    deleted = false,
                                    childComments = listOf()
                                ),
                                Comment(
                                    id = 3,
                                    author = member,
                                    content = "부모댓글1에 대한 자식댓글2",
                                    createdAt = LocalDateTime.of(2023, 7, 25, 22, 1, 5),
                                    updatedAt = LocalDateTime.of(2023, 7, 25, 22, 1, 5),
                                    deleted = false,
                                    childComments = listOf()
                                )
                            )
                        )
                    )
                )
            }
        )
    }

    @Test
    @DisplayName("네트워크 통신이 원활하면 ApiModel을 파싱한 특정 댓글에 달린 대댓글들을 불러올 수 있다")
    fun test2() = runTest {
        val commentId = 1L
        val childCommentApiModels = listOf(
            CommentApiModel(
                commentId = 2,
                memberId = 1,
                memberName = "홍길동",
                memberImageUrl = "https://naver.com",
                content = "부모댓글1에 대한 자식댓글1",
                parentId = commentId,
                eventId = 1,
                createdAt = "2023:07:25:22:01:05",
                updatedAt = "2023:07:25:22:01:05",
                deleted = false
            ),
            CommentApiModel(
                commentId = 3,
                memberId = 1,
                memberName = "홍길동",
                memberImageUrl = "https://naver.com",
                content = "부모댓글1에 대한 자식댓글2",
                parentId = commentId,
                eventId = 1,
                createdAt = "2023:07:25:22:01:05",
                updatedAt = "2023:07:25:22:01:05",
                deleted = false
            )
        )
        coEvery { commentService.getChildComments(commentId) } returns Response.success(
            childCommentApiModels
        )
        val memberId = 1L
        val member = Member1(
            id = memberId,
            name = "홍길동",
            description = "",
            imageUrl = "https://naver.com",
            activities = listOf()
        )
        coEvery { memberRepository.getMember(memberId) } returns ApiSuccess(member)

        val childComments = sut.getChildComments(commentId)

        assertAll(
            { assertThat(childComments).isInstanceOf(ApiSuccess::class.java) },
            {
                assertThat((childComments as ApiSuccess).data).isEqualTo(
                    listOf(
                        Comment(
                            id = 2,
                            author = member,
                            content = "부모댓글1에 대한 자식댓글1",
                            createdAt = LocalDateTime.of(2023, 7, 25, 22, 1, 5),
                            updatedAt = LocalDateTime.of(2023, 7, 25, 22, 1, 5),
                            deleted = false,
                            childComments = listOf()
                        ),
                        Comment(
                            id = 3,
                            author = member,
                            content = "부모댓글1에 대한 자식댓글2",
                            createdAt = LocalDateTime.of(2023, 7, 25, 22, 1, 5),
                            updatedAt = LocalDateTime.of(2023, 7, 25, 22, 1, 5),
                            deleted = false,
                            childComments = listOf()
                        )
                    )
                )
            }
        )
    }
}