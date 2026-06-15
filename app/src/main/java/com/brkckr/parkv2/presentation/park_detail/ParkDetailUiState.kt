package com.brkckr.parkv2.presentation.park_detail

import com.brkckr.parkv2.domain.model.ParkDetail
import com.brkckr.parkv2.domain.util.UiText

data class ParkDetailUiState(
    val isLoading: Boolean = false,
    val parkDetail: ParkDetail? = null,
    val error: UiText? = null
)