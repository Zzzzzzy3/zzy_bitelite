package com.example.dashdine.di

import com.example.dashdine.data.repository.AppRepository
import com.example.dashdine.data.repository.MockAppRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindAppRepository(
        mockAppRepository: MockAppRepository
    ): AppRepository
}
