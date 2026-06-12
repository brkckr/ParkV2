package com.brkckr.parkv2.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.brkckr.parkv2.data.local.dao.ParkDao
import com.brkckr.parkv2.data.local.entity.ParkEntity

@Database(entities = [ParkEntity::class], version = 1, exportSchema = false)
abstract class ParkDatabase : RoomDatabase() {
    abstract val dao: ParkDao
}
