package com.emmsale.presentation.ui.eventdetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.model.EventDetail
import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.presentation.ui.eventDetail.EventDetailViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.Headers
import org.assertj.core.api.SoftAssertions
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class EventDetailViewModelTest {

    private lateinit var vm: EventDetailViewModel
    private lateinit var eventRepository: EventRepository

    private val testEventDetail = EventDetail(
        id = 1L,
        name = "Florence Joseph",
        informationUrl = "https://search.yahoo.com/search?p=mediocrem",
        startDate = LocalDateTime.now(),
        endDate = LocalDateTime.now(),
        applyStartDate = LocalDateTime.now(),
        applyEndDate = LocalDateTime.now(),
        location = "hinc",
        status = "tamquam",
        applyStatus = "graecis",
        tags = listOf(),
        posterImageUrl = null,
        remainingDays = 9431,
        applyRemainingDays = 2123,
        type = "minim",
    )

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        eventRepository = mockk(relaxed = true)
        vm = EventDetailViewModel(1L, eventRepository)
    }

    @After
    fun finish() {
        Dispatchers.resetMain()
    }

    @Test
    fun `정상적으로 상세정보를 받아온다면, id가 1인 상세정보를 요청했을 때, eventDetail 은 Error와 Loading 상태가 false 이고 id가 1이다 `() {
        // given
        coEvery {
            eventRepository.getEventDetail(1L)
        } answers {
            Success(testEventDetail, Headers.headersOf("Auth", "hi"))
        }

        // when
        vm.refresh()

        // then
        val softly = SoftAssertions().apply {
            assertThat(vm.eventDetail.value.isError).isEqualTo(false)
            assertThat(vm.eventDetail.value.isLoading).isEqualTo(false)
            assertThat(vm.eventDetail.value.id).isEqualTo(1L)
        }
        softly.assertAll()
    }

    @Test
    fun `정상적으로 상세정보를 받아올 수 없다면, id가 1인 상세정보를 요청했을 때, eventDetail은 Error 상태가 된다 `() {
        // given
        coEvery {
            eventRepository.getEventDetail(1L)
        } answers {
            Failure(code = 4548, message = null)
        }

        // when
        vm.refresh()

        // then
        val softly = SoftAssertions().apply {
            assertThat(vm.eventDetail.value.isError).isEqualTo(true)
            assertThat(vm.eventDetail.value.isLoading).isEqualTo(false)
        }
        softly.assertAll()
    }
}
