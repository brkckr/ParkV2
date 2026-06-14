package com.brkckr.parkv2.domain.usecase

import com.brkckr.parkv2.domain.repository.ParkRepository
import javax.inject.Inject

// updates the favorite status of a specific park
class ToggleFavoriteUseCase @Inject constructor(
    private val repository: ParkRepository
) {
    suspend operator fun invoke(parkId: Int, isFavorite: Boolean) {
        repository.toggleFavorite(parkId, isFavorite)
    }
}
