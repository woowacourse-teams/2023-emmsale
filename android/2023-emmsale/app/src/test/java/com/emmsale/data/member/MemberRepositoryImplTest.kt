package com.emmsale.data.member

import com.emmsale.data.activity.Activity1
import com.emmsale.data.activity.ActivityType
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.member.dto.MemberActivitiesBindActivityTypeApiModel
import com.emmsale.data.member.dto.MemberActivityApiModel
import com.emmsale.data.member.dto.MemberWithoutActivitiesApiModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import retrofit2.Response

internal class MemberRepositoryImplTest {

    private lateinit var memberService: MemberService
    private lateinit var sut: MemberRepositoryImpl

    private val memberApiModelFixture = MemberWithoutActivitiesApiModel(
        id = 1L,
        name = "토마스",
        description = "",
        imageUrl = ""
    )

    @BeforeEach
    fun setUp() {
        memberService = mockk()
        sut = MemberRepositoryImpl(memberService = memberService)
    }

    @Test
    @DisplayName("네트워크 통신이 원활하면 회원 객체가 포함된 ApiSuccess 객체를 반환한다")
    fun test1() = runTest {
        val memberId = 1L
        val apiModel = memberApiModelFixture.copy(memberId)
        coEvery { memberService.getMember(memberId) } returns Response.success(apiModel)
        coEvery { memberService.getActivities() } returns Response.success(
            listOf(
                MemberActivitiesBindActivityTypeApiModel(
                    "동아리",
                    listOf(
                        MemberActivityApiModel(
                            id = 1L,
                            name = "DDD 5기"
                        ),
                        MemberActivityApiModel(
                            id = 2L,
                            name = "SOPT 13기"
                        )
                    )
                ),
                MemberActivitiesBindActivityTypeApiModel(
                    "직무",
                    listOf(
                        MemberActivityApiModel(
                            id = 3L,
                            name = "Backend"
                        ),
                        MemberActivityApiModel(
                            id = 4L,
                            name = "Frontend"
                        )
                    )
                )
            )
        )

        val result = sut.getMember(memberId)

        assertAll(
            { assertThat(result).isInstanceOf(ApiSuccess::class.java) },
            {
                assertThat((result as ApiSuccess).data).isEqualTo(
                    Member1(
                        id = memberId,
                        name = "토마스",
                        description = "",
                        imageUrl = "",
                        activities = mapOf(
                            ActivityType.CLUB to listOf(
                                Activity1(
                                    id = 1L,
                                    activityType = ActivityType.CLUB,
                                    name = "DDD 5기"
                                ),
                                Activity1(
                                    id = 2L,
                                    activityType = ActivityType.CLUB,
                                    name = "SOPT 13기"
                                )
                            ),
                            ActivityType.JOB to listOf(
                                Activity1(
                                    id = 3L,
                                    activityType = ActivityType.JOB,
                                    name = "Backend"
                                ),
                                Activity1(
                                    id = 4L,
                                    activityType = ActivityType.JOB,
                                    name = "Frontend"
                                )
                            )
                        )
                    )
                )
            }
        )
    }
}
