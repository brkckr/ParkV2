package com.brkckr.parkv2.presentation.park_detail

import com.brkckr.parkv2.domain.model.ParkDetail

sealed interface ParkDetailAction {
    data class LoadParkDetail(val parkId: Int) : ParkDetailAction
    data class ToggleFavorite(val park: ParkDetail) : ParkDetailAction
    data object NavigateBack : ParkDetailAction
}
