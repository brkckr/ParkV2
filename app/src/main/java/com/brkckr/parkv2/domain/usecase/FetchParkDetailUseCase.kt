package com.brkckr.parkv2.domain.usecase

import com.brkckr.parkv2.domain.model.ParkDetail
import com.brkckr.parkv2.domain.repository.ParkRepository
import com.brkckr.parkv2.domain.util.Resource
import javax.inject.Inject

// fetches the detailed information of a specific park
class FetchParkDetailUseCase @Inject constructor(
    private val repository: ParkRepository
) {
    suspend operator fun invoke(parkId: Int): Resource<ParkDetail> {
        return repository.fetchParkDetail(parkId)
    }
}