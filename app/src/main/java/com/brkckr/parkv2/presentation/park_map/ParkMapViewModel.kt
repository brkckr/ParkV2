package com.brkckr.parkv2.presentation.park_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brkckr.parkv2.R
import com.brkckr.parkv2.domain.model.Park
import com.brkckr.parkv2.domain.model.ParkFilter
import com.brkckr.parkv2.domain.usecase.FetchParksUseCase
import com.brkckr.parkv2.domain.usecase.FilterParksUseCase
import com.brkckr.parkv2.domain.usecase.ObserveFavoriteParksUseCase
import com.brkckr.parkv2.domain.usecase.ObserveParksUseCase
import com.brkckr.parkv2.domain.usecase.ToggleFavoriteUseCase
import com.brkckr.parkv2.domain.util.Resource
import com.brkckr.parkv2.domain.util.UiText
import com.brkckr.parkv2.presentation.common.UiEvent
import com.brkckr.parkv2.ui.theme.Animations
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class ParkMapViewModel @Inject constructor(
    private val fetchParksUseCase: FetchParksUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val observeFavoriteParksUseCase: ObserveFavoriteParksUseCase,
    private val observeParksUseCase: ObserveParksUseCase,
    private val filterParksUseCase: FilterParksUseCase
) : ViewModel() {

    private val _allParks = MutableStateFlow<List<Park>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _activeFilter = MutableStateFlow(ParkFilter.ALL)
    private val _selectedPark = MutableStateFlow<Park?>(null)
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<UiText?>(null)

    // handles one-time ui events like showing snackbars
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    // adds a small delay to search input to reduce unnecessary filtering
    private val _debouncedSearchQuery = _searchQuery
        .debounce(Animations.SearchDebounce.milliseconds)
        .onStart { emit("") }

    // combine states for ui
    // combines multiple states to create a single ui state flow
    val state: StateFlow<ParkMapUiState> = combine(
        _allParks,
        _searchQuery,
        _debouncedSearchQuery,
        _activeFilter,
        _selectedPark,
        _userLocation,
        _favoriteIds,
        _isLoading,
        _error
    ) { args ->
        val allParks = args[0] as List<Park>
        val rawQuery = args[1] as String
        val debouncedQuery = args[2] as String
        val filter = args[3] as ParkFilter
        val selected = args[4] as Park?
        val location = args[5] as LatLng?
        val favIds = args[6] as Set<Int>
        val loading = args[7] as Boolean
        val err = args[8] as UiText?

        ParkMapUiState(
            filteredParks = filterParksUseCase(
                allParks,
                debouncedQuery,
                filter,
                favIds,
                location
            ),
            searchQuery = rawQuery,
            activeFilter = filter,
            selectedPark = selected,
            userLocation = location,
            isLoading = loading,
            error = err
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(Animations.StateSharingTimeout),
        ParkMapUiState()
    )

    init {
        loadParks()
        observeParks()
        observeFavorites()
    }

    // processes user interactions from the main screen
    fun onAction(action: ParkMapAction) {
        when (action) {
            is ParkMapAction.OnSearchQueryChange -> _searchQuery.value = action.query
            is ParkMapAction.OnFilterChange -> {
                _activeFilter.value =
                    if (_activeFilter.value == action.filter) ParkFilter.ALL else action.filter
            }

            is ParkMapAction.OnParkSelected -> _selectedPark.value = action.park
            is ParkMapAction.OnUserLocationChanged -> _userLocation.value = action.location
            is ParkMapAction.ToggleFavorite -> toggleFavorite(action.park)
            ParkMapAction.RetryLoadParks -> loadParks()
        }
    }

    // fetches the initial list of parks from the repository
    private fun loadParks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val result = fetchParksUseCase()) {
                is Resource.Success -> {
                    _isLoading.value = false
                }

                is Resource.Error -> {
                    _error.value = result.message
                    _isLoading.value = false
                }

                Resource.Loading -> _isLoading.value = true
            }
        }
    }

    // observes the complete list of parks from the database
    private fun observeParks() {
        observeParksUseCase()
            .distinctUntilChanged()
            .onEach { parks ->
                _allParks.value = parks
            }
            .launchIn(viewModelScope)
    }

    // observes changes in favorite parks to update the ui
    private fun observeFavorites() {
        viewModelScope.launch {
            observeFavoriteParksUseCase().collect { favorites ->
                _favoriteIds.value = favorites.map { it.parkID }.toSet()
            }
        }
    }

    // updates the favorite status of a park and shows a notification
    private fun toggleFavorite(park: Park) {
        // handle favorite logic
        viewModelScope.launch {
            val newFavorite = !park.isFavorite
            toggleFavoriteUseCase(park.parkID, newFavorite)
            val uiText = if (newFavorite) {
                UiText.StringResource(R.string.added_to_favorites)
            } else {
                UiText.StringResource(R.string.removed_from_favorites)
            }
            _uiEvent.send(UiEvent.ShowSnackbar(uiText))
        }
    }
}