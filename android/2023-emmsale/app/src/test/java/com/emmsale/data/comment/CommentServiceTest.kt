package com.emmsale.data.comment

import com.emmsale.data.comment.dto.CommentFamilyApiModel
import com.emmsale.data.comment.dto.CommentApiModel
import com.emmsale.data.comment.dto.SaveCommentRequestBody
import com.emmsale.data.comment.dto.UpdateCommentRequestBody
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import retrofit2.Retrofit

internal class CommentServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var url: HttpUrl
    private lateinit var sut: CommentService
    private lateinit var okHttpClient: OkHttpClient

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        url = mockWebServer.url("/")

        val jsonMediaType = "application/json".toMediaType()
        val json = Json {
            coerceInputValues = true
            encodeDefaults = true
            isLenient = true
        }
        val jsonConverterFactory = json.asConverterFactory(jsonMediaType)

        sut = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(jsonConverterFactory)
            .build()
            .create(CommentService::class.java)

        okHttpClient = OkHttpClient()
    }

    @Test
    @DisplayName("특정 행사의 댓글을 요청했을 때 성공적으로 응답을 받았다면 파싱된 ApiModel을 받는다")
    fun test1() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
                    [
                        {
                            "parentComment": {
                                "content": "부모댓글2",
                                "commentId": 4,
                                "parentId": null,
                                "eventId": 1,
                                "createdAt": "2023:07:25:22:01:05",
                                "updatedAt": "2023:07:25:22:01:05",
                                "deleted": false,
                                "memberId": 1,
                                "memberName": "홍길동",
                                "memberImageUrl": "https://naver.com"
                            },
                            "childComments": [ ]
                        },
                        {
                            "parentComment": {
                                "content": "부모댓글1",
                                "commentId": 5,
                                "parentId": null,
                                "eventId": 1,
                                "createdAt": "2023:07:25:22:01:05",
                                "updatedAt": "2023:07:25:22:01:05",
                                "deleted": false,
                                "memberId": 1,
                                "memberName": "홍길동",
                                "memberImageUrl": "https://naver.com"
                            },
                            "childComments": [
                                {
                                    "content": "부모댓글1에 대한 자식댓글1",
                                    "commentId": 2,
                                    "parentId": 1,
                                    "eventId": 1,
                                    "createdAt": "2023:07:25:22:01:05",
                                    "updatedAt": "2023:07:25:22:01:05",
                                    "deleted": false,
                                    "memberId": 1,
                                    "memberName": "홍길동",
                                    "memberImageUrl": "https://naver.com"
                                },
                                {
                                    "content": "부모댓글1에 대한 자식댓글2",
                                    "commentId": 3,
                                    "parentId": 1,
                                    "eventId": 1,
                                    "createdAt": "2023:07:25:22:01:05",
                                    "updatedAt": "2023:07:25:22:01:05",
                                    "deleted": false,
                                    "memberId": 1,
                                    "memberName": "홍길동",
                                    "memberImageUrl": "https://naver.com"
                                }
                            ]
                        }
                    ]
                """.trimIndent()
            )
        mockWebServer.enqueue(mockResponse)

        val anyEventId = 1L
        val response = sut.getComments(anyEventId)

        assertThat(response.body()).isEqualTo(
            listOf(
                CommentFamilyApiModel(
                    CommentApiModel(
                        commentId = 4,
                        memberId = 1,
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
                        memberId = 1,
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
                            memberId = 1,
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
                            memberId = 1,
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
        )
    }

    @Test
    @DisplayName("특정 댓글의 대댓글을 요청했을 때 성공적으로 응답을 받았다면 파싱된 ApiModel을 받는다")
    fun test2() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
                    [
                        {
                            "content" : "부모댓글1에 대한 자식댓글1",
                            "commentId" : 2,
                            "parentId" : 1,
                            "eventId" : 1,
                            "createdAt" : "2023:08:01:08:53:08",
                            "updatedAt" : "2023:08:01:08:53:08",
                            "memberId" : 1,
                            "memberImageUrl" : "이미지",
                            "memberName" : "이름1",
                            "deleted" : false
                        },
                        {
                            "content" : "부모댓글1에 대한 자식댓글2",
                            "commentId" : 3,
                            "parentId" : 1,
                            "eventId" : 1,
                            "createdAt" : "2023:08:01:08:53:08",
                            "updatedAt" : "2023:08:01:08:53:08",
                            "memberId" : 1,
                            "memberImageUrl" : "이미지",
                            "memberName" : "이름1",
                            "deleted" : false
                        }
                    ]
                """.trimIndent()
            )
        mockWebServer.enqueue(mockResponse)

        val commentId = 1L
        val response = sut.getChildComments(commentId)

        assertThat(response.body()).isEqualTo(
            listOf(
                CommentApiModel(
                    content = "부모댓글1에 대한 자식댓글1",
                    commentId = 2,
                    parentId = 1,
                    eventId = 1,
                    createdAt = "2023:08:01:08:53:08",
                    updatedAt = "2023:08:01:08:53:08",
                    memberId = 1,
                    memberImageUrl = "이미지",
                    memberName = "이름1",
                    deleted = false
                ),
                CommentApiModel(
                    content = "부모댓글1에 대한 자식댓글2",
                    commentId = 3,
                    parentId = 1,
                    eventId = 1,
                    createdAt = "2023:08:01:08:53:08",
                    updatedAt = "2023:08:01:08:53:08",
                    memberId = 1,
                    memberImageUrl = "이미지",
                    memberName = "이름1",
                    deleted = false
                )
            )
        )
    }

    @Test
    @DisplayName("특정 행사의 댓글을 게시했을 때 성공적으로 반영되었다면 파싱된 ApiModel을 받는다")
    fun test3() = runTest {
        val content = "ㅎㅇㅎㅇ"
        val eventId = 1L
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
                    {
                        "content": $content,
                        "commentId": 4,
                        "parentId": null,
                        "eventId": $eventId,
                        "createdAt": "2023:07:25:22:01:05",
                        "updatedAt": "2023:07:25:22:01:05",
                        "deleted": false,
                        "memberId": 1,
                        "memberName": "홍길동",
                        "memberImageUrl": "https://naver.com"
                    }
                """.trimIndent()
            )
        mockWebServer.enqueue(mockResponse)

        val saveCommentRequestBody = SaveCommentRequestBody(content, eventId, null)
        val response = sut.saveComment(saveCommentRequestBody)

        assertThat(response.body()).isEqualTo(
            CommentApiModel(
                content = content,
                commentId = 4,
                parentId = null,
                eventId = eventId,
                createdAt = "2023:07:25:22:01:05",
                updatedAt = "2023:07:25:22:01:05",
                deleted = false,
                memberId = 1,
                memberName = "홍길동",
                memberImageUrl = "https://naver.com"
            ),
        )
    }

    @Test
    @DisplayName("특정 댓글의 내용을 수정했을 때 성공적으로 반영되었다면 파싱된 ApiModel을 받는다")
    fun test4() = runTest {
        val content = "ㅎㅇㅎㅇ"
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
                    {
                        "content": $content,
                        "commentId": 4,
                        "parentId": null,
                        "eventId": 1,
                        "createdAt": "2023:07:25:22:01:05",
                        "updatedAt": "2023:07:25:22:01:05",
                        "deleted": false,
                        "memberId": 1,
                        "memberName": "홍길동",
                        "memberImageUrl": "https://naver.com"
                    }
                """.trimIndent()
            )
        mockWebServer.enqueue(mockResponse)

        val response = sut.updateComment(4, UpdateCommentRequestBody(content))

        assertThat(response.body()).isEqualTo(
            CommentApiModel(
                content = content,
                commentId = 4,
                parentId = null,
                eventId = 1,
                createdAt = "2023:07:25:22:01:05",
                updatedAt = "2023:07:25:22:01:05",
                deleted = false,
                memberId = 1,
                memberName = "홍길동",
                memberImageUrl = "https://naver.com"
            ),
        )
    }

    @Test
    @DisplayName("특정 댓글의 내용을 삭제했을 때 성공적으로 반영되었다면 body가 null인 성공적인 Response 객체를 받는다")
    fun test5() = runTest {
        val commentId = 1L
        val mockResponse = MockResponse()
            .setResponseCode(204)
        mockWebServer.enqueue(mockResponse)

        val response = sut.deleteComment(commentId)

        assertAll(
            { assertThat(response.isSuccessful).isTrue },
            { assertThat(response.body()).isNull() }
        )
    }
}