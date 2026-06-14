package com.brkckr.parkv2.domain.usecase

import com.brkckr.parkv2.domain.model.Park
import com.brkckr.parkv2.domain.repository.ParkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// observes the complete list of parks from the database
class ObserveParksUseCase @Inject constructor(
    private val repository: ParkRepository
) {
    operator fun invoke(): Flow<List<Park>> {
        return repository.observeParks()
    }
}
