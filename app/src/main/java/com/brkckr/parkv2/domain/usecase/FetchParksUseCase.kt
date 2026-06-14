package com.brkckr.parkv2.domain.usecase

import com.brkckr.parkv2.domain.model.Park
import com.brkckr.parkv2.domain.repository.ParkRepository
import com.brkckr.parkv2.domain.util.Resource
import javax.inject.Inject

// fetches all available parks from the repository
class FetchParksUseCase @Inject constructor(
    private val repository: ParkRepository
) {
    suspend operator fun invoke(): Resource<List<Park>> {
        return repository.fetchParks()
    }
}
