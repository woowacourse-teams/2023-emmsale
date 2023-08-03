package com.emmsale.data.member

import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.member.dto.ActivitiesAssociatedByActivityTypeApiModel
import com.emmsale.data.member.dto.ActivityApiModel
import com.emmsale.data.member.dto.MemberWithoutActivitiesApiModel
import com.emmsale.data.member.mapper.toData
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
        val memberApiModel = memberApiModelFixture.copy(memberId)
        coEvery { memberService.getMember(memberId) } returns Response.success(memberApiModel)
        val activitiesApiModel = listOf(
            ActivitiesAssociatedByActivityTypeApiModel(
                "동아리",
                listOf(
                    ActivityApiModel(
                        id = 1L,
                        name = "DDD 5기"
                    ),
                    ActivityApiModel(
                        id = 2L,
                        name = "SOPT 13기"
                    )
                )
            ),
            ActivitiesAssociatedByActivityTypeApiModel(
                "직무",
                listOf(
                    ActivityApiModel(
                        id = 3L,
                        name = "Backend"
                    ),
                    ActivityApiModel(
                        id = 4L,
                        name = "Frontend"
                    )
                )
            )
        )
        coEvery { memberService.getActivities(memberId) } returns Response.success(
            activitiesApiModel
        )

        val result = sut.getMember(memberId)

        assertAll(
            { assertThat(result).isInstanceOf(ApiSuccess::class.java) },
            {
                assertThat((result as ApiSuccess).data).isEqualTo(
                    memberApiModel.toData(activitiesApiModel)
                )
            }
        )
    }
}
