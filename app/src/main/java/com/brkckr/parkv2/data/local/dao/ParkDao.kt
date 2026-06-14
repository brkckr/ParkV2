package com.brkckr.parkv2.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.brkckr.parkv2.data.local.entity.ParkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkDao {

    @Query("SELECT * FROM parks")
    fun observeParks(): Flow<List<ParkEntity>>

    @Query("SELECT * FROM parks")
    suspend fun getParks(): List<ParkEntity>

    @Query("SELECT * FROM parks WHERE isFavorite = 1")
    fun observeFavoriteParks(): Flow<List<ParkEntity>>

    @Query("SELECT * FROM parks WHERE parkID = :parkId")
    suspend fun getParkById(parkId: Int): ParkEntity?

    @Query("SELECT * FROM parks WHERE parkID = :parkId")
    fun observeParkById(parkId: Int): Flow<ParkEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParks(parks: List<ParkEntity>)

    @Query("UPDATE parks SET isFavorite = :isFavorite WHERE parkID = :parkId")
    suspend fun updateFavoriteStatus(parkId: Int, isFavorite: Boolean)

    @Query("SELECT isFavorite FROM parks WHERE parkID = :parkId")
    suspend fun isFavorite(parkId: Int): Boolean?
}
