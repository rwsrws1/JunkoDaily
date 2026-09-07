package com.example.learncompose.core.data.di

import com.example.learncompose.core.data.api.RoutineRepoApi
import com.example.learncompose.core.data.repository.OfflineRoutineRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RoutineRepoModule {

    @Binds
    abstract fun bindOfflineRoutineRepo(
        offlineRoutineRepo: OfflineRoutineRepo
    ) : RoutineRepoApi
}