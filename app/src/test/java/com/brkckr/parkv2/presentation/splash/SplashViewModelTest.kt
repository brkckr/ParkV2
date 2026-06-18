package com.brkckr.parkv2.presentation.splash

import app.cash.turbine.test
import com.brkckr.parkv2.domain.usecase.FetchParksUseCase
import com.brkckr.parkv2.domain.util.Resource
import com.brkckr.parkv2.presentation.common.UiEvent
import com.brkckr.parkv2.util.MainDispatcherExtension
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(MainDispatcherExtension::class)
class SplashViewModelTest {

    private lateinit var fetchParksUseCase: FetchParksUseCase

    @BeforeEach
    fun setUp() {
        fetchParksUseCase = mockk()
    }

    @Test
    fun `initialization calls fetchParks and navigates to home on success`() = runTest {
        // Given
        coEvery { fetchParksUseCase() } returns Resource.Success(emptyList())

        // When & Then
        val viewModel = SplashViewModel(fetchParksUseCase)
        
        viewModel.uiEvent.test {
            assertThat(awaitItem()).isEqualTo(UiEvent.NavigateToHome)
        }
    }

    @Test
    fun `initialization sets state correctly on success`() = runTest {
        // Given
        coEvery { fetchParksUseCase() } returns Resource.Success(emptyList())

        // When
        val viewModel = SplashViewModel(fetchParksUseCase)

        // Then
        viewModel.uiState.test {
            val finalState = awaitItem()
            assertThat(finalState.isLoading).isFalse()
            assertThat(finalState.error).isNull()
        }
    }

    @Test
    fun `initialization sets error state on failure`() = runTest {
        // Given
        val errorText = com.brkckr.parkv2.domain.util.UiText.DynamicString("Error")
        coEvery { fetchParksUseCase() } returns Resource.Error(errorText)

        // When
        val viewModel = SplashViewModel(fetchParksUseCase)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isEqualTo(errorText)
        }
    }
}
