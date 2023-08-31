package com.emmsale.presentation.ui.eventdetail.information

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.scrap.ScrappedEventRepository
import com.emmsale.presentation.ui.eventdetail.information.uiState.EventInfoUiEvent
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.Headers
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(Parameterized::class)
class EventInfoViewModelTest(private val isScrappedFromRepository: Boolean) {
    private lateinit var vm: EventInfoViewModel
    private lateinit var scrappedEventRepository: ScrappedEventRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        scrappedEventRepository = mockk()
        vm = EventInfoViewModel(scrappedEventRepository = scrappedEventRepository, eventId = 0)
    }

    @After
    fun finish() {
        Dispatchers.resetMain()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            arrayOf(true),
            arrayOf(false),
        )
    }

    @Test
    fun `정상적으로 스크랩 여부를 받아온다면, 스크랩 상태를 받아온 값으로 업데이트하고, 오류 상태가 아니다`() {
        // given
        coEvery {
            scrappedEventRepository.isScraped(0)
        } answers {
            ApiSuccess(isScrappedFromRepository, Headers.headersOf("Auth", "Hi"))
        }
        // when
        vm.refresh()
        // then
        val softly = SoftAssertions().apply {
            assertThat(vm.isScraped.value).isEqualTo(isScrappedFromRepository)
            assertThat(vm.isError.value).isEqualTo(false)
        }
        softly.assertAll()
    }

    @Test
    fun `정상적으로 스크랩 여부를 받아오지 못한다면, 오류 상태가 된다`() {
        // given
        coEvery {
            scrappedEventRepository.isScraped(0)
        } answers {
            ApiSuccess(isScrappedFromRepository, Headers.headersOf("Auth", "Hi"))
        }
        // when
        vm.refresh()
        // then
        assertThat(vm.isError.value).isEqualTo(false)
    }

    @Test
    fun `정상적으로 스크랩을 성공한다면, 스크랩 상태는 true 이다`() {
        // given
        coEvery {
            scrappedEventRepository.isScraped(0)
        } answers {
            ApiSuccess(false, Headers.headersOf("Auth", "Hi"))
        }
        coEvery {
            scrappedEventRepository.scrapEvent(0)
        } answers {
            ApiSuccess(Unit, Headers.headersOf("default", "default"))
        }
        // when
        vm.scrapEvent()
        // then
        assertThat(vm.isScraped.value).isEqualTo(true)
    }

    @Test
    fun `정상적으로 스크랩을 성공못한다면, 스크랩 실패 이벤트로 업데이트한다`() {
        // given
        coEvery {
            scrappedEventRepository.isScraped(0)
        } answers {
            ApiSuccess(false, Headers.headersOf("Auth", "Hi"))
        }
        coEvery {
            scrappedEventRepository.scrapEvent(0)
        } answers {
            ApiError(code = 6226, message = null)
        }
        // when
        vm.scrapEvent()
        // then
        assertThat(vm.event.value).isEqualTo(EventInfoUiEvent.SCRAP_ERROR)
    }

    @Test
    fun `정상적으로 스크랩을 해제한다면, 스크랩 상태는 false 이다`() {
        // given
        coEvery {
            scrappedEventRepository.isScraped(0)
        } answers {
            ApiSuccess(true, Headers.headersOf("Auth", "Hi"))
        }
        coEvery {
            scrappedEventRepository.deleteScrap(0)
        } answers {
            ApiSuccess(Unit, Headers.headersOf("default", "default"))
        }
        // when
        vm.deleteScrap()
        // then
        assertThat(vm.isScraped.value).isEqualTo(false)
    }

    @Test
    fun `정상적으로 스크랩을 해제 못한다면, 삭제 실패 이벤트로 업데이트 한다`() {
        // given
        coEvery {
            scrappedEventRepository.isScraped(0)
        } answers {
            ApiSuccess(true, Headers.headersOf("Auth", "Hi"))
        }
        coEvery {
            scrappedEventRepository.deleteScrap(0)
        } answers {
            ApiError(code = 2253, message = null)
        }
        // when
        vm.deleteScrap()
        // then
        assertThat(vm.event.value).isEqualTo(EventInfoUiEvent.SCRAP_DELETE_ERROR)
    }
}
