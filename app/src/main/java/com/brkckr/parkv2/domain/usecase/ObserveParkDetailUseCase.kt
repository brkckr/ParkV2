package com.brkckr.parkv2.domain.usecase

import com.brkckr.parkv2.domain.model.Park
import com.brkckr.parkv2.domain.repository.ParkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// observes a specific park's data changes
class ObserveParkDetailUseCase @Inject constructor(
    private val repository: ParkRepository
) {
    operator fun invoke(parkId: Int): Flow<Park?> {
        return repository.observeParkDetail(parkId)
    }
}
