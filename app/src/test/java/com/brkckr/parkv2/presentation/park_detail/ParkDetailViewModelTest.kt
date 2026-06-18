package com.brkckr.parkv2.presentation.park_detail

import app.cash.turbine.test
import com.brkckr.parkv2.domain.model.Park
import com.brkckr.parkv2.domain.model.ParkDetail
import com.brkckr.parkv2.domain.model.ParkStatus
import com.brkckr.parkv2.domain.usecase.FetchParkDetailUseCase
import com.brkckr.parkv2.domain.usecase.ObserveParkDetailUseCase
import com.brkckr.parkv2.domain.usecase.ToggleFavoriteUseCase
import com.brkckr.parkv2.domain.util.Resource
import com.brkckr.parkv2.domain.util.UiText
import com.brkckr.parkv2.presentation.common.UiEvent
import com.brkckr.parkv2.util.MainDispatcherExtension
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(MainDispatcherExtension::class)
class ParkDetailViewModelTest {

    private lateinit var viewModel: ParkDetailViewModel
    private lateinit var fetchParkDetailUseCase: FetchParkDetailUseCase
    private lateinit var observeParkDetailUseCase: ObserveParkDetailUseCase
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase

    private val sampleParkDetail = ParkDetail(
        parkID = 1,
        parkName = "Sample Park",
        lat = 41.0,
        lng = 28.0,
        capacity = 100,
        emptyCapacity = 20,
        isOpen = true,
        parkType = "Open",
        district = "Fatih",
        workHours = "24/7",
        freeTime = "15m",
        fee = "50 TL",
        monthlyFee = "1500 TL",
        areaPoints = emptyList(),
        address = "Address",
        phone = "123",
        status = ParkStatus.OPEN,
        isFavorite = false
    )

    @BeforeEach
    fun setUp() {
        fetchParkDetailUseCase = mockk()
        observeParkDetailUseCase = mockk()
        toggleFavoriteUseCase = mockk()

        // Default behavior
        every { observeParkDetailUseCase(any()) } returns flowOf(null)
    }

    @Test
    fun `LoadParkDetail updates state with park detail on success`() = runTest {
        // Given
        coEvery { fetchParkDetailUseCase(1) } returns Resource.Success(sampleParkDetail)
        viewModel = ParkDetailViewModel(
            fetchParkDetailUseCase,
            observeParkDetailUseCase,
            toggleFavoriteUseCase
        )

        // When
        viewModel.onAction(ParkDetailAction.LoadParkDetail(1))

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.parkDetail).isEqualTo(sampleParkDetail)
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `ToggleFavorite calls use case and sends event`() = runTest {
        // Given
        coEvery { fetchParkDetailUseCase(any()) } returns Resource.Success(sampleParkDetail)
        coEvery { toggleFavoriteUseCase(any(), any()) } returns Unit
        viewModel = ParkDetailViewModel(
            fetchParkDetailUseCase,
            observeParkDetailUseCase,
            toggleFavoriteUseCase
        )

        // When
        viewModel.onAction(ParkDetailAction.ToggleFavorite(sampleParkDetail))

        // Then
        coVerify { toggleFavoriteUseCase(sampleParkDetail.parkID, true) }
        viewModel.uiEvent.test {
            val event = awaitItem()
            assertThat(event).isInstanceOf(UiEvent.ShowSnackbar::class.java)
        }
    }
}
