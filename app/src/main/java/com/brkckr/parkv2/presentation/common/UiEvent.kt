package com.brkckr.parkv2.presentation.common

import com.brkckr.parkv2.domain.util.UiText

sealed class UiEvent {
    data class ShowSnackbar(val message: UiText) : UiEvent()
    object NavigateBack : UiEvent()
    object NavigateToHome : UiEvent()
}
