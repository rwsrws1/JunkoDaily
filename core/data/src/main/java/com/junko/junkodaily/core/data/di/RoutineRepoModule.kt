package com.junko.junkodaily.core.data.di

import com.junko.junkodaily.core.data.api.RoutineRepoApi
import com.junko.junkodaily.core.data.repository.OfflineRoutineRepo
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