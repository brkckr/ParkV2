package com.brkckr.parkv2.di

import android.content.Context
import androidx.room.Room
import com.brkckr.parkv2.data.local.ParkDatabase
import com.brkckr.parkv2.data.local.dao.ParkDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ParkDatabase {
        return Room.databaseBuilder(
            context,
            ParkDatabase::class.java,
            "park_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDao(db: ParkDatabase): ParkDao = db.dao
}
