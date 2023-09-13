package com.emmsale.data.member

import com.emmsale.data.apiModel.response.MemberApiModel
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.mapper.toData
import com.emmsale.data.repository.DefaultMemberRepository
import com.emmsale.data.service.MemberService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import retrofit2.Response

internal class DefaultMemberRepositoryTest {

    private lateinit var memberService: MemberService
    private lateinit var sut: DefaultMemberRepository

    private val memberApiModelFixture = MemberApiModel(
        id = 1L,
        name = "토마스",
        description = "",
        imageUrl = "",
    )

    @BeforeEach
    fun setUp() {
        memberService = mockk()
        sut = DefaultMemberRepository(memberService = memberService)
    }

    @Test
    @DisplayName("네트워크 통신이 원활하면 회원 객체가 포함된 ApiSuccess 객체를 반환한다")
    fun test1() = runTest {
        val memberId = 1L
        val memberApiModel = memberApiModelFixture.copy(memberId)
        coEvery { memberService.getMember(memberId) } returns Response.success(memberApiModel)

        val result = sut.getMember(memberId)

        assertAll(
            { assertThat(result).isInstanceOf(ApiSuccess::class.java) },
            {
                assertThat((result as ApiSuccess).data).isEqualTo(
                    memberApiModel.toData(),
                )
            },
        )
    }
}
