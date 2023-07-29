package com.emmsale.data.member

import com.emmsale.data.member.dto.MemberActivitiesBindActivityTypeApiModel
import com.emmsale.data.member.dto.MemberActivityApiModel
import com.emmsale.data.member.dto.MemberApiModel1
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

internal class MemberServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var url: HttpUrl
    private lateinit var sut: MemberService
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
            .create(MemberService::class.java)

        okHttpClient = OkHttpClient()
    }

    @Test
    @DisplayName("특정 회원 정보를 요청했을 때 성공적으로 응답을 받았다면 MemberApiModel로 파싱된 데이터를 반환한다")
    fun test1() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
                    {
                        "id": 4,
                        "name": "THOMAS",
                        "description": null,
                        "imageUrl": "https://avatars.githubusercontent.com/u/38097088?v=4"
                    }
                """.trimIndent()
            )
        mockWebServer.enqueue(mockResponse)

        val response = sut.fetchMember(4L)

        assertThat(response.body()).isEqualTo(
            MemberApiModel1(
                id = 4L,
                name = "THOMAS",
                description = "",
                imageUrl = "https://avatars.githubusercontent.com/u/38097088?v=4"
            )
        )
    }

    @Test
    @DisplayName("특정 회원이 했던 활동 정보를 요청했을 때 성공적으로 응답을 받았다면 MemberActivitiesBindActivityTypeApiModel로 파싱된 데이터를 반환한다")
    fun test2() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
                    [ {
                      "activityType" : "동아리",
                      "memberActivityResponses" : [ {
                        "id" : 1,
                        "name" : "YAPP"
                      }, {
                        "id" : 2,
                        "name" : "DND"
                      }, {
                        "id" : 3,
                        "name" : "nexters"
                      } ]
                    }, {
                      "activityType" : "컨퍼런스",
                      "memberActivityResponses" : [ {
                        "id" : 4,
                        "name" : "인프콘"
                      } ]
                    }, {
                      "activityType" : "교육",
                      "memberActivityResponses" : [ {
                        "id" : 5,
                        "name" : "우아한테크코스"
                      } ]
                    }, {
                      "activityType" : "직무",
                      "memberActivityResponses" : [ {
                        "id" : 6,
                        "name" : "Backend"
                      } ]
                    } ]
                """.trimIndent()
            )
        mockWebServer.enqueue(mockResponse)

        val response = sut.fetchActivities()

        assertThat(response.body()).isEqualTo(
            listOf(
                MemberActivitiesBindActivityTypeApiModel(
                    activityType = "동아리",
                    memberActivityResponses = listOf(
                        MemberActivityApiModel(
                            id = 1,
                            name = "YAPP"
                        ),
                        MemberActivityApiModel(
                            id = 2,
                            name = "DND",
                        ),
                        MemberActivityApiModel(
                            id = 3,
                            name = "nexters"
                        )
                    )
                ),
                MemberActivitiesBindActivityTypeApiModel(
                    activityType = "컨퍼런스",
                    memberActivityResponses = listOf(
                        MemberActivityApiModel(
                            id = 4,
                            name = "인프콘"
                        )
                    )
                ),
                MemberActivitiesBindActivityTypeApiModel(
                    activityType = "교육",
                    memberActivityResponses = listOf(
                        MemberActivityApiModel(
                            id = 5,
                            name = "우아한테크코스"
                        )
                    )
                ),
                MemberActivitiesBindActivityTypeApiModel(
                    activityType = "직무",
                    memberActivityResponses = listOf(
                        MemberActivityApiModel(
                            id = 6,
                            name = "Backend"
                        )
                    )
                )
            )
        )
    }
}
