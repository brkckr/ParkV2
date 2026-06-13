package com.brkckr.parkv2.data.remote.api

import com.brkckr.parkv2.data.remote.model.ParkDetailResponse
import com.brkckr.parkv2.data.remote.model.ParkResponse
import com.brkckr.parkv2.domain.util.Resource
import retrofit2.http.GET
import retrofit2.http.Query

interface ParkApiService {

    @GET("Park")
    suspend fun getParks(): Resource<List<ParkResponse>>

    @GET("ParkDetay")
    suspend fun getParkDetail(
        @Query("id") parkId: Int
    ): Resource<List<ParkDetailResponse>>
}
