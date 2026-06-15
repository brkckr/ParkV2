package com.brkckr.parkv2.presentation.park_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brkckr.parkv2.R
import com.brkckr.parkv2.domain.model.ParkDetail
import com.brkckr.parkv2.domain.usecase.FetchParkDetailUseCase
import com.brkckr.parkv2.domain.usecase.ObserveParkDetailUseCase
import com.brkckr.parkv2.domain.usecase.ToggleFavoriteUseCase
import com.brkckr.parkv2.domain.util.Resource
import com.brkckr.parkv2.domain.util.UiText
import com.brkckr.parkv2.presentation.common.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParkDetailViewModel @Inject constructor(
    private val fetchParkDetailUseCase: FetchParkDetailUseCase,
    private val observeParkDetailUseCase: ObserveParkDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    // manages the ui state for park details
    private val _uiState = MutableStateFlow(ParkDetailUiState())
    val uiState = _uiState.asStateFlow()

    // handles ui events for navigation and user feedback
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    // processes actions related to park details and favorites
    fun onAction(action: ParkDetailAction) {
        when (action) {
            is ParkDetailAction.LoadParkDetail -> loadParkDetail(action.parkId)
            is ParkDetailAction.ToggleFavorite -> toggleFavorite(action.park)
            ParkDetailAction.NavigateBack -> {
                viewModelScope.launch { _uiEvent.send(UiEvent.NavigateBack) }
            }
        }
    }

    // fetches park details and observes local database changes
    private fun loadParkDetail(parkId: Int) {
        // fetch details and sync with local db
        observeParkDetailUseCase(parkId)
            .onEach { park ->
                _uiState.update { state ->
                    val updatedDetail =
                        state.parkDetail?.copy(isFavorite = park?.isFavorite ?: false)
                    state.copy(parkDetail = updatedDetail)
                }
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = fetchParkDetailUseCase(parkId)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            parkDetail = result.data
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }

                else -> Unit
            }
        }
    }

    // toggles the favorite status of the current park
    private fun toggleFavorite(parkDetail: ParkDetail) {
        viewModelScope.launch {
            val newFavorite = !parkDetail.isFavorite
            toggleFavoriteUseCase(parkDetail.parkID, newFavorite)

            val message =
                if (newFavorite) R.string.added_to_favorites else R.string.removed_from_favorites
            _uiEvent.send(UiEvent.ShowSnackbar(UiText.StringResource(message)))
        }
    }
}