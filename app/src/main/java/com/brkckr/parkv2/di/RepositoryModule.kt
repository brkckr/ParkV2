package com.brkckr.parkv2.di

import com.brkckr.parkv2.data.repository.ParkRepositoryImpl
import com.brkckr.parkv2.domain.repository.ParkRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindParkRepository(
        parkRepositoryImpl: ParkRepositoryImpl
    ): ParkRepository
}
