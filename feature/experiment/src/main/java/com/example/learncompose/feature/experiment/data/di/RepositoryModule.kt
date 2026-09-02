package com.example.learncompose.feature.experiment.data.di

import com.example.learncompose.feature.experiment.data.repo.AuthRepositoryImpl
import com.example.learncompose.feature.experiment.data.repo.IAuthRepository
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
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): IAuthRepository
}