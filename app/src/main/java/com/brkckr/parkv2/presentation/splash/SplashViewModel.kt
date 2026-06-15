package com.brkckr.parkv2.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brkckr.parkv2.domain.usecase.FetchParksUseCase
import com.brkckr.parkv2.domain.util.Resource
import com.brkckr.parkv2.presentation.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val fetchParksUseCase: FetchParksUseCase
) : ViewModel() {

    // manages the ui state of the splash screen
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    // handles ui events like navigation and snackbars
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        onAction(SplashAction.LoadParks)
    }

    // process actions coming from the splash screen
    fun onAction(action: SplashAction) {
        when (action) {
            SplashAction.LoadParks -> loadParks()
        }
    }

    // loads initial park data and navigates to main screen
    private fun loadParks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = fetchParksUseCase()) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.send(UiEvent.NavigateToHome)
                }

                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }

                Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                }
            }
        }
    }
}
