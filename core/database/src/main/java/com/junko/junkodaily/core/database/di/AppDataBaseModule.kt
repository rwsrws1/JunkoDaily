package com.junko.junkodaily.core.database.di

import android.content.Context
import androidx.room.Room
import com.junko.junkodaily.core.database.AppDatabase
import com.junko.junkodaily.core.database.UserDataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDataBaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "mydaily_database"
        ).build()
    }

    @Provides
    fun provideUserDataDao(database: AppDatabase): UserDataDao {
        return database.userDataDao()
    }
}