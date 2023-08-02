package com.emmsale.data.comment

import com.emmsale.data.comment.dto.CommentFamilyApiModel
import com.emmsale.data.comment.dto.CommentApiModel
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
}