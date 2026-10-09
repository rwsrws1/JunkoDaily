package com.junko.junkodaily.feature.experiment.data.di

import android.content.Context
import androidx.room.Room
import com.junko.junkodaily.feature.experiment.data.room.AppDatabase
import com.junko.junkodaily.feature.experiment.data.room.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_database"
        )
//            .addMigrations(MIGRATION_3_4)
//            .addMigrations(MIGRATION_4_5)
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
}