package com.brkckr.parkv2.presentation.park_map

import app.cash.turbine.test
import com.brkckr.parkv2.domain.model.Park
import com.brkckr.parkv2.domain.model.ParkFilter
import com.brkckr.parkv2.domain.model.ParkStatus
import com.brkckr.parkv2.domain.usecase.FetchParksUseCase
import com.brkckr.parkv2.domain.usecase.FilterParksUseCase
import com.brkckr.parkv2.domain.usecase.ObserveFavoriteParksUseCase
import com.brkckr.parkv2.domain.usecase.ObserveParksUseCase
import com.brkckr.parkv2.domain.usecase.ToggleFavoriteUseCase
import com.brkckr.parkv2.domain.util.Resource
import com.brkckr.parkv2.util.MainDispatcherExtension
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
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
class ParkMapViewModelTest {

    private lateinit var viewModel: ParkMapViewModel
    private lateinit var fetchParksUseCase: FetchParksUseCase
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase
    private lateinit var observeFavoriteParksUseCase: ObserveFavoriteParksUseCase
    private lateinit var observeParksUseCase: ObserveParksUseCase
    private lateinit var filterParksUseCase: FilterParksUseCase

    private val sampleParks = listOf(
        Park(
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
            status = ParkStatus.OPEN
        )
    )

    @BeforeEach
    fun setUp() {
        fetchParksUseCase = mockk()
        toggleFavoriteUseCase = mockk()
        observeFavoriteParksUseCase = mockk()
        observeParksUseCase = mockk()
        filterParksUseCase = mockk()

        coEvery { fetchParksUseCase() } returns Resource.Success(emptyList())
        every { observeParksUseCase() } returns flowOf(sampleParks)
        every { observeFavoriteParksUseCase() } returns flowOf(emptyList())
        every { filterParksUseCase(any(), any(), any(), any(), any()) } returns sampleParks
    }

    @Test
    fun `initialization loads parks and observes updates`() = runTest {
        // When
        viewModel = ParkMapViewModel(
            fetchParksUseCase,
            toggleFavoriteUseCase,
            observeFavoriteParksUseCase,
            observeParksUseCase,
            filterParksUseCase
        )

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.filteredParks).isEqualTo(sampleParks)
        }
    }

    @Test
    fun `OnSearchQueryChange updates state`() = runTest {
        // Given
        viewModel = ParkMapViewModel(
            fetchParksUseCase,
            toggleFavoriteUseCase,
            observeFavoriteParksUseCase,
            observeParksUseCase,
            filterParksUseCase
        )

        // When
        viewModel.onAction(ParkMapAction.OnSearchQueryChange("test"))

        // Then
        viewModel.state.test {
            assertThat(awaitItem().searchQuery).isEqualTo("test")
        }
    }

    @Test
    fun `OnFilterChange updates state`() = runTest {
        // Given
        viewModel = ParkMapViewModel(
            fetchParksUseCase,
            toggleFavoriteUseCase,
            observeFavoriteParksUseCase,
            observeParksUseCase,
            filterParksUseCase
        )

        // When
        viewModel.onAction(ParkMapAction.OnFilterChange(ParkFilter.AVAILABLE))

        // Then
        viewModel.state.test {
            assertThat(awaitItem().activeFilter).isEqualTo(ParkFilter.AVAILABLE)
        }
    }
}
