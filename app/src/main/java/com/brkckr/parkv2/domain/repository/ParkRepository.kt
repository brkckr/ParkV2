package com.brkckr.parkv2.domain.repository

import com.brkckr.parkv2.domain.model.Park
import com.brkckr.parkv2.domain.model.ParkDetail
import com.brkckr.parkv2.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ParkRepository {
    suspend fun fetchParks(): Resource<List<Park>>
    suspend fun fetchParkDetail(parkId: Int): Resource<ParkDetail>
    suspend fun toggleFavorite(parkId: Int, isFavorite: Boolean)
    fun observeParks(): Flow<List<Park>>
    fun observeFavoriteParks(): Flow<List<Park>>
    fun observeParkDetail(parkId: Int): Flow<Park?>
}
