package com.brkckr.parkv2.data.repository

import com.brkckr.parkv2.R
import com.brkckr.parkv2.data.local.dao.ParkDao
import com.brkckr.parkv2.data.mapper.toDomain
import com.brkckr.parkv2.data.mapper.toEntity
import com.brkckr.parkv2.data.remote.api.ParkApiService
import com.brkckr.parkv2.domain.model.Park
import com.brkckr.parkv2.domain.model.ParkDetail
import com.brkckr.parkv2.domain.repository.ParkRepository
import com.brkckr.parkv2.domain.util.Resource
import com.brkckr.parkv2.domain.util.UiText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// implementation of park repository managing data from network and local database
class ParkRepositoryImpl @Inject constructor(
    private val apiService: ParkApiService,
    private val parkDao: ParkDao
) : ParkRepository {

    // fetches parks from api and caches them locally while preserving favorites
    override suspend fun fetchParks(): Resource<List<Park>> {
        val localParks = parkDao.getParks()

        return when (val networkResult = apiService.getParks()) {
            is Resource.Success -> {
                val networkData = networkResult.data
                val favoriteIds = localParks.filter { it.isFavorite }.map { it.parkID }.toSet()

                val entities = networkData.map { response ->
                    response.toEntity().copy(isFavorite = response.parkID in favoriteIds)
                }

                parkDao.insertParks(entities)
                Resource.Success(parkDao.getParks().map { it.toDomain() })
            }

            is Resource.Error -> {
                if (localParks.isNotEmpty()) {
                    Resource.Success(localParks.map { it.toDomain() })
                } else {
                    Resource.Error(networkResult.message)
                }
            }

            is Resource.Loading -> Resource.Loading
        }
    }

    // fetches detailed park info and updates local storage
    override suspend fun fetchParkDetail(parkId: Int): Resource<ParkDetail> {
        val localPark = parkDao.getParkById(parkId)
        val isFavorite = localPark?.isFavorite ?: false
        val localIsOpen = localPark?.isOpen == 1

        return when (val networkResult = apiService.getParkDetail(parkId)) {
            is Resource.Success -> {
                val detailResponse = networkResult.data.firstOrNull()
                if (detailResponse != null) {
                    val detail = detailResponse.toDomain(isFavorite, localIsOpen)

                    localPark?.let { entity ->
                        parkDao.insertParks(
                            listOf(
                                entity.copy(
                                    emptyCapacity = detail.emptyCapacity,
                                    isOpen = if (detail.isOpen) 1 else 0
                                )
                            )
                        )
                    }

                    Resource.Success(detail)
                } else {
                    Resource.Error(UiText.StringResource(R.string.park_details_not_found))
                }
            }

            is Resource.Error -> Resource.Error(networkResult.message)
            is Resource.Loading -> Resource.Loading
        }
    }

    // updates the favorite status of a park in the local database
    override suspend fun toggleFavorite(parkId: Int, isFavorite: Boolean) {
        parkDao.updateFavoriteStatus(parkId, isFavorite)
    }

    // provides a reactive flow of all parks from the local database
    override fun observeParks(): Flow<List<Park>> {
        return parkDao.observeParks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // provides a reactive flow of favorite parks from the local database
    override fun observeFavoriteParks(): Flow<List<Park>> {
        return parkDao.observeFavoriteParks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // provides a reactive flow of a specific park's data
    override fun observeParkDetail(parkId: Int): Flow<Park?> {
        return parkDao.observeParkById(parkId).map { it?.toDomain() }
    }
}
